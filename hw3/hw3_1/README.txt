There are 2 options inside java code to do the task.

Or you can use the queries below:

var filteredStudents=db.students.aggregate([{$unwind : "$scores"}, {$match: {"scores.type" : "homework"}}, {$group: {"_id":"$_id", "minscore":{$min:"$scores.score"}}}] )
filteredStudents.forEach(function(doc) { db.students.update({"_id":doc._id}, {$pull: {scores : { "score":doc.minscore, "type":"homework"}}});});