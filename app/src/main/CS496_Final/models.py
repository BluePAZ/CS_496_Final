from google.appengine.ext import db

class User(db.Model):
    username = db.StringProperty(default = '');
    chatMates = db.ListProperty(db.Key);
    chats = db.ListProperty(db.Key);
    
class Chat(db.Model):
    user1Id = db.StringProperty();
    user2Id = db.StringProperty();
    user1PublicKey = db.IntegerProperty();
    user2PublicKey = db.IntegerProperty();
    generator = db.IntegerProperty();
    
class Message(db.Model):
    dateSent = db.DateTimeProperty();
    data = db.StringProperty();
    chat = db.ReferenceProperty(Chat, collection_name='messages');
    senderKey = db.StringProperty();
    receiverKey = db.StringProperty();
