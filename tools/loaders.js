//Main Loop
// Author: Kylin Soong(kylinsoong.1214@gmail.com)

// Loop times
loopSize = 200

// Parameters used by MQL
nameValue = 'BOCNET-G-BMSVR'
taskidValue = 'BOCNET-G-BMSVR_TERM_TXNID_TPS'
logTimeGTEValue = '20190725121145'
logTimeLTEValue = '20190725121149'

// App Name
appname = "app"
item = Math.random().toString(36).substring(7);

print()
print("[" + item + "] start simulating business load ...")
print()

dbName = db.getName()

// A Business scenario usually related with several queries
// eg, the following 5 queries are used in a real time dashboard
var bizQueries = []

query_1 = [{$match: {$and: [{name: nameValue}, {taskid: taskidValue}, {logTime: {$gte: logTimeGTEValue, $lte: logTimeLTEValue}}]}}, {$project: {byMinute: {$substr: ['$logTime', 0, 12]}, value: 1, _id: 0 }}, {$group: {_id: '$byMinute', avgByMinute: {$avg: '$value'}}}, {$sort: {   _id: 1 }}]
query_2 = [{$match: {$and: [{name: nameValue}, {taskid: taskidValue}, {logTime: {$gte: logTimeGTEValue, $lte: logTimeLTEValue}}]}}, {$project: {byMinute: {$substr: ['$logTime', 0, 12]}, value: 1, _id: 0 }}, {$group: {_id: '$byMinute', avgByMinute: {$avg: '$value'}}}, {$sort: {   _id: 1 }}]
query_3 = [{$match: {$and: [{name: nameValue}, {taskid: taskidValue}, {logTime: {$gte: logTimeGTEValue, $lte: logTimeLTEValue}}]}}, {$project: {byMinute: {$substr: ['$logTime', 0, 12]}, value: 1, _id: 0 }}, {$group: {_id: '$byMinute', avgByMinute: {$avg: '$value'}}}, {$sort: {   _id: 1 }}]
query_4 = db.bmsvr_amount.find({name: "BOCNET-G-BMSVR"})
query_5 = db.bmsvr_amount.find({taskid: "BOCNET-G-BMSVR_TERM_TXNID_TPS"})

bizQueries.push({coll: "bmsvr_tps", mql: query_1, isAggregate: 1})
bizQueries.push({coll: "bmsvr_tra", mql: query_2, isAggregate: 1})
bizQueries.push({coll: "bmsvr_pki", mql: query_3, isAggregate: 1})
bizQueries.push({coll: "bmsvr_amount", mql: query_4})
bizQueries.push({coll: "bmsvr_amount", mql: query_5})

var start = new Date();

for (var i = 0 ; i < loopSize ; i ++) {

  for(c = 0; c< bizQueries.length; c++) {

    bizQuery = bizQueries[c]
    bizQueryColl = bizQuery.coll
    bizQueryMQL = bizQuery.mql
    bizQueryAggregate = bizQuery.isAggregate
    if(bizQueryAggregate == 1) {
      cursor = db.getCollection(bizQueryColl).aggregate(bizQueryMQL);
      while ( cursor.hasNext() ) {
        cursor.next();
      }
    } else {
      cursor = bizQueryMQL
      while ( cursor.hasNext() ) {
        cursor.next();
      }
    }
  }
}

var end = new Date();

var totalTime = (end - start);
var avgTime = (totalTime/loopSize)

results = {name:appname, threads:item, loop:loopSize, opcount:(loopSize*bizQueries.length), start:start, end:end, totalTime:totalTime, avgTime:avgTime}
db.TMP_RESULTS_STATS.insert(results)

print()
print("[" + item + "] end, loader loops " + loopSize + " times")

printjson(results)

p = [{$group: { _id: '$name', totalLoop: {$sum: '$loop'}, totalDBOps: {$sum: '$opcount'}, totalTime: {$avg: '$totalTime'}, avgTime: {$avg: '$avgTime'}}}]
print()
print("[" + item + "] " +  appname + " loader final aggregate results")
cursor = db.TMP_RESULTS_STATS.aggregate(p);
while ( cursor.hasNext() ) {
  printjson( cursor.next() );
}


