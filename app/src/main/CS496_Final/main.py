#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2
import json
import models
import logging
import datetime

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello world!')
        
class RegisterNewUser(webapp2.RequestHandler):
    def post(self):
        postData = json.loads(self.request.body);
        logging.info(self.request.body);
        allUsers = models.User().all();
        exist = False;
        for us in allUsers:
            if us.username == postData['username']:
                exist = True;
                self.error(500)
        if exist != True:
            user = models.User();
            user.username = postData['username'];
            user.put();
            self.response.write(user.key())
        else:
            self.error(500)
class VerifyUserKey(webapp2.RequestHandler):
        def post(self):
            logging.info("Verifying User Key")
            logging.info(self.request.body)
            postData = self.request.body;
            logging.info(self.request.body);
            #logging.info(models.User.get(postData));
            try:
                usr = models.User.get(postData)
                if models.User.get(postData) is None:
                    self.error(500)
                else:
                    self.response.set_status(204)
            except:
                self.error(500)
                
class ReturnPosse(webapp2.RequestHandler):
        def post(self):
            users = models.User.all();
            ret = list();
            logging.info(users);
            for u in users:
                ret.append({'username':u.username, 'keyValue':str(u.key())});
            logging.info(ret);
            self.response.write(json.dumps(ret));
            
class StartChat(webapp2.RequestHandler):
        def post(self):
            postData = self.request.body;
            postData = json.loads(postData);
            senderKey = postData['sourceKey'];
            receiverKey = postData['destKey'];
            chasts = models.Chat.all()
            for c in chasts:
                if c.user1Id == receiverKey and c.user2Id == senderKey:
                    self.response.write(c.key())
                else:
                    chat = models.Chat();
                    chat.user1Id = senderKey;
                    chat.user2Id = receiverKey;
                    chat.put();
                    self.response.write(chat.key());
class ReceiveMessage(webapp2.RequestHandler):
    def post(self):
        postData = self.request.body;
        logging.info(postData);
        postData = json.loads(postData);
        chatKey = postData['chatKey'];
        senderKey = postData['senderKey'];
        receiverKey = postData['receiverKey'];
        chat = None;
        if chatKey in [None, "NULL"]:
            chasts = models.Chat.all()
            for c in chasts:
                if (c.user1Id == receiverKey and c.user2Id == senderKey) or (c.user1Id == senderKey and c.user2Id == receiverKey):
                    chat = c;
                    logging.info("FOUND CHAT")
                    break;
        else:
            chat = models.Chat.get(chatKey)
        if chat in [None, "NULL"]:
            chat = models.Chat();
            chat.user1Id = senderKey;
            chat.user2Id = receiverKey;
            chat.put();
        msg = models.Message();
        msg.data = postData['content'];
        if msg.data != '':
            msg.senderKey = senderKey;
            msg.receiverKey = receiverKey;
            msg.dateSent = datetime.datetime.now()
            msg.chat = chat;
            msg.put();
        self.response.write(chat.key());
        
class GetMessages(webapp2.RequestHandler):
    def post(self):
        postData = json.loads(self.request.body)
        if(postData['chatKey'] not in ["", None]):
            chat = models.Chat.get(postData['chatKey'])
        if chat is None:
            self.error(500)
        else:
            messages = chat.messages.order('dateSent');
            ret = list();
            for m in messages:
                ret.append({'senderKey':m.senderKey, 'receiverKey':m.receiverKey, 'content':m.data})
            self.response.write(json.dumps(ret))  
        
        
app = webapp2.WSGIApplication([
('/', MainHandler),
 ('/Register', RegisterNewUser),
 ('/VerifyUserKey', VerifyUserKey),
 ('/RetrievePosseList', ReturnPosse),
 ('/StartChat', StartChat),
 ('/GetMessages', GetMessages),
 ('/ReceiveMessage', ReceiveMessage)
 ], debug=True)
