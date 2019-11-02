//
// Version: 0.1
// Author: Kylin Soong(kylinsoong.1214@gmail.com)
//

prefix = "[" + Math.random().toString(36).substring(7) + "] "
sleepTime = 1000

print()
print(prefix + "replica set health check start")
print()

print(prefix + "replication info:")
print();
sleep(sleepTime);
info = db.getReplicationInfo()
print("configured oplog size:   " + info.logSizeMB + " MB");
print("used oplog size:         " + info.usedMB + " MB");
print("log length start to end: " + info.timeDiff + " secs (" + info.timeDiffHours + " hrs)");
print("oplog first event time:  " + info.tFirst);
print("oplog last event time:   " + info.tLast);
print("now:                     " + info.now);
print();

print(prefix + "replication lag");
print();
sleep(sleepTime);
rs.printSlaveReplicationInfo()
print();

print(prefix + "replication oplog window");
print();
sleep(sleepTime);
var opEnd = db.getSiblingDB("local").oplog.rs.find().sort({$natural: -1}).limit(1).next().ts.getTime()
var opStart = db.getSiblingDB("local").oplog.rs.find().sort({$natural: 1}).limit(1).next().ts.getTime()
windowsecs =  (opEnd - opStart)
windowhrs = (windowsecs/3600).toFixed(2)
print(windowsecs + " secs (" + windowhrs + " hrs)");

print();
print();
print();
print();
print();


