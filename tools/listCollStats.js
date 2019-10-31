//Main Loopo
// Version: V0.2
// Author: Kylin Soong(kylinsoong.1214@gmail.com)

limitSize = 20;
sleepTime = 1000 

print()
print("list collections stats started ...")
print()

dbName = db.getName()
print("current database: [" + dbName + "]")
print()

collectionNames = db.getCollectionNames()
for(c=0;c<collectionNames.length;c++) {
  collectionName = collectionNames[c]
  collStats = db[collectionName].stats()
  db.TMP_LIST_COLL_STATS.insert(collStats)
  print("collect stats: [" + collectionName + "]")
}
print()

print("list top " + limitSize + " collections by count")
sleep(sleepTime);
cursor = db.TMP_LIST_COLL_STATS.find({}, {_id: 0, ns: 1, count: 1}).sort({count: -1}).limit(limitSize)
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}
print()

print("list top " + limitSize + " collections by size")
sleep(sleepTime);
cursor = db.TMP_LIST_COLL_STATS.find({}, {_id: 0, ns: 1, size: 1}).sort({size: -1}).limit(limitSize)
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}
print()

print("list top " + limitSize + " collections by storageSize")
sleep(sleepTime);
cursor = db.TMP_LIST_COLL_STATS.find({}, {_id: 0, ns: 1, storageSize: 1}).sort({storageSize: -1}).limit(limitSize)
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}
print()

print("list top " + limitSize + " collections by avgObjSize")
sleep(sleepTime);
cursor = db.TMP_LIST_COLL_STATS.find({}, {_id: 0, ns: 1, avgObjSize: 1}).sort({avgObjSize: -1}).limit(limitSize)
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}
print()

print("list top " + limitSize + " collections by nindexes")
sleep(sleepTime);
cursor = db.TMP_LIST_COLL_STATS.find({}, {_id: 0, ns: 1, nindexes: 1}).sort({nindexes: -1}).limit(limitSize)
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}
print()

print("list top " + limitSize + " collections by totalIndexSize")
sleep(sleepTime);
cursor = db.TMP_LIST_COLL_STATS.find({}, {_id: 0, ns: 1, totalIndexSize: 1}).sort({totalIndexSize: -1}).limit(limitSize)
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}
print()

print("list the final summary")
sleep(sleepTime);
p = [{$group: {_id: null, totalSize: {$sum: '$size'}, totalDocuments: {$sum: '$count'}, totalStorageSize: {$sum: '$storageSize'}, totalIndexSize: {$sum: '$totalIndexSize'}}}, {$project: {_id: 0, totalSize:1, totalDocuments: 1, totalStorageSize: 1, totalIndexSize: 1 }}]
cursor = db.TMP_LIST_COLL_STATS.aggregate(p)
results = cursor.next()
results.dbName = dbName
results.totalCollections = collectionNames.length
printjson(results)

db.TMP_LIST_COLL_STATS.drop()

print()
sleep(1000);
