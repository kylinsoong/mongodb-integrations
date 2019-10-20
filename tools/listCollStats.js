//Main Loop

print()
print("List collections by count/size/storageSize started...")
print()

dbName = db.getName()
print("Current database: [" + dbName + "]")
print()

conn = new Mongo();
db = conn.getDB(dbName);

collectionInfos = []

collectionNames = db.getCollectionNames()
for(c=0;c<collectionNames.length;c++) {
  //print(collectionNames[c])
  collectionInfos.push({name:collectionNames[c],count:0,size:0,storageSize:0})
}

for(c=0;c<collectionInfos.length;c++) {
  collInfo = collectionInfos[c]
  mb = 1024*1024
  collStats = db[collInfo.name].stats()
 // printjson(collStats)
  size = collStats["size"]
  count = collStats["count"]
  avgObjSize = collStats["avgObjSize"]
  storageSize = collStats["storageSize"]
  nindexes = collStats["nindexes"]
  totalIndexSize = collStats["totalIndexSize"]

 // print(count + ", " + size + ", " + storageSize )
  collectionInfos[c].count = count
  collectionInfos[c].size = size
  collectionInfos[c].storageSize = storageSize
}

//printjson(collectionInfos)

db.TMPLISTCOLLSTATS.insertMany(collectionInfos)

print("List collections by count")
cursor = db.TMPLISTCOLLSTATS.find({}, {_id: 0, name: 1, count: 1}).sort({count: -1})
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}

print()
print("List collections by size")
cursor = db.TMPLISTCOLLSTATS.find({}, {_id: 0, name: 1, size: 1}).sort({size: -1})
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}

print()
print("List collections by storageSize")
cursor = db.TMPLISTCOLLSTATS.find({}, {_id: 0, name: 1, storageSize: 1}).sort({storageSize: -1})
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}

db.TMPLISTCOLLSTATS.drop()

