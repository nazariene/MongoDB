Add these indexes:

db.posts.createIndex({"permalink":1});
db.posts.createIndex({"date":1});
db.posts.createIndex({"tags":1});    
