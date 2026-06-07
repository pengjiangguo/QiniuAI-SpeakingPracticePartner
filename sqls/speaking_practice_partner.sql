/*
 Navicat Premium Data Transfer

 Source Server         : project
 Source Server Type    : MySQL
 Source Server Version : 80300 (8.3.0)
 Source Host           : localhost:3306
 Source Schema         : speaking_practice_partner

 Target Server Type    : MySQL
 Target Server Version : 80300 (8.3.0)
 File Encoding         : 65001

 Date: 07/06/2026 23:44:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_messages
-- ----------------------------
DROP TABLE IF EXISTS `chat_messages`;
CREATE TABLE `chat_messages`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色 (USER/ASSISTANT/SYSTEM)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `audio_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '音频路径',
  `asr_confidence` double NULL DEFAULT NULL COMMENT 'ASR识别置信度',
  `pronunciation_score` int NULL DEFAULT NULL COMMENT '发音评分',
  `grammar_error_count` int NULL DEFAULT NULL COMMENT '语法错误数量',
  `corrected` int NULL DEFAULT 0 COMMENT '是否已纠错 (0-否 1-是)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对话消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_messages
-- ----------------------------
INSERT INTO `chat_messages` VALUES ('007a26f61a1355e7d14fad275941e61d', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'ASSISTANT', 'Oh, it seems like you\'re thinking about how to express yourself! Take your time — there\'s no rush.\n\n💡 小提示：A single word like \"the\" isn\'t a complete sentence. Try using it in a full sentence.\n✅ 正确表达：The weather is nice today. / The movie was great.\n\nWant to try again? What were you going to say?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('011525d42d1f334e1d18f5fe3932aa73', '59443ede71139fed669372621b5eb78d', 'ASSISTANT', 'That\'s great! Being a student is such an exciting time. What are you studying?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('01276a54e56bf3f0e3d1b56fa366442c', '4f65f097a39818b446485a6329d26964', 'USER', 'The the.', NULL, NULL, 2, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('0bedd3ddcea21c5cd6a225e80eec46f3', '836ebc109f6bf770ccd8d5e278478a8f', 'ASSISTANT', 'That\'s an interesting question! I\'m an AI, so I don\'t really have an age like humans do. But I was created to help people like you learn English! How old are you, if you don\'t mind me asking?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('1226af7d3108c433896a7c25458bc8ac', '70741bd4e7dccd293c48b6476efd752b', 'USER', 'The.', NULL, NULL, 0, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('18c92ea5e56e93cc7f463f5dfbad484b', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'USER', 'Love!', NULL, NULL, 15, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('1a3ff5983cc8ee2bd2982e8029ef6587', 'ae3482c6c3341f77227260c212a62960', 'ASSISTANT', 'Haha, no, I don\'t have a mouth to chew with! 😄 But I can tell you about chewing. When we eat, we chew food to break it down. Do you like foods that are crunchy or soft?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('1a5a68836f636f23a8ded8750b27470c', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'USER', 'No, what are you doing now?', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('1a5e6e956c50f4f6f06637169080b226', '728713856d37812198441282c97fc8a8', 'USER', 'What are you doing?', NULL, NULL, 91, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('1b2e7e4a904de080780149d439eb62d4', '70741bd4e7dccd293c48b6476efd752b', 'USER', 'The.', NULL, NULL, 0, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('1f1fe985f10bb551142edfc6496a39e4', 'ae3482c6c3341f77227260c212a62960', 'ASSISTANT', 'That\'s perfectly fine! Take your time to think. 😊\n---\n💡 小提示：在回答问题时，\"I\'ll think about it\" 听起来更自然。\n✅ 正确表达：I\'ll think about it.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('2394ca80a9ad8bb6b7ec496edacb3ed2', '00a630ecb75637356b59e87b09501b88', 'ASSISTANT', 'Hey there! I\'m just chatting with you right now. How about you? What are you up to at the moment?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('37481d6e3ce113ebea77f8c00c015ae6', '70741bd4e7dccd293c48b6476efd752b', 'ASSISTANT', 'Hey there! I\'m just hanging out and chatting with you, helping you practice your English. What about you? What are you up to right now?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('3d083c8cd6211f823f9a3c5057ab2c74', '728713856d37812198441282c97fc8a8', 'ASSISTANT', 'Hey there! I\'m just chilling and ready to chat with you. How\'s your day going so far?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('4150f013ceea4fe1defa8689e0cda6d9', '70741bd4e7dccd293c48b6476efd752b', 'ASSISTANT', 'It looks like you might be having a little trouble getting started. That\'s totally okay! Let me help you out.\n\nTry saying: \"I am a...\" or \"I work as a...\" — just fill in the blank with what you do. For example, \"I am a student\" or \"I work as a teacher.\"\n\nWhat would you like to say?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('43f9d3ae308b03f351a18cb936d47bf9', '00a630ecb75637356b59e87b09501b88', 'USER', 'Are you doing now?', NULL, NULL, 98, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('4477dc753678339fefc90f87c9ef8264', 'ae3482c6c3341f77227260c212a62960', 'USER', 'Two.', NULL, NULL, 0, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('484f9411ed6b4a194fcb705702a01bd5', '4f65f097a39818b446485a6329d26964', 'ASSISTANT', 'I see you\'re thinking about what to say! Don\'t worry, take your time. 😊\n\nLet me help you start. You can say something like:\n- \"I\'d like to order...\"\n- \"Could I see the menu?\"\n- \"What do you recommend?\"\n\nWould you like to try ordering something? For example: \"I\'d like to have a steak, please.\"', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('5171eed0ab7dc09a0874d005e8d53dca', '728713856d37812198441282c97fc8a8', 'ASSISTANT', 'Oh, you went to school yesterday! Did you have any interesting classes?\n---\n💡 小提示：描述过去发生的事要用过去时态哦。\n✅ 正确表达：I went to school yesterday.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('520c6c1caa658b671492c1f778a8eeac', '70741bd4e7dccd293c48b6476efd752b', 'ASSISTANT', 'Oh, you\'re asking about my job! I\'m an AI speaking partner, so my \"work\" is to chat with people like you and help them improve their English. Pretty cool, right?\n\nBut what about you? Do you work or study?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('540a264f11ad630b9afd1020d893ce1c', 'ae3482c6c3341f77227260c212a62960', 'ASSISTANT', 'Oh, you went to school yesterday! That\'s good. What did you learn or do there?\n---\n💡 小提示：过去的事情要用过去时态哦。\"go\" 的过去式是 \"went\"，而且 \"yesterday\" 表示过去时间。\n✅ 正确表达：I went to school yesterday.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('549e53421c82053b5df7779085123ba7', 'ae3482c6c3341f77227260c212a62960', 'USER', 'Do you chew it?', NULL, NULL, 92, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('556aaf0084715bb5ecb86054086c1f3c', 'ae3482c6c3341f77227260c212a62960', 'ASSISTANT', 'I think you mean \"me too\"! 😊 It\'s a common small mistake.\n---\n💡 小提示：\"Two\" 和 \"too\" 发音很像，但含义不同。\"Two\" 是数字2，\"too\" 意思是 \"也\"。\n✅ 正确表达：Me too!', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('62c056efac56c62086f3ab1b8cabcfd7', '956a534fc962f5aed8051ffe05e5c672', 'USER', 'The.', NULL, NULL, 19, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('6331057073b3d5f566205a224403fbb1', '4f65f097a39818b446485a6329d26964', 'USER', 'I\'m eating.', NULL, NULL, 82, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('699a3439288db049518c631e15373754', '30bf57737b0ec0c617446aaa2327eddf', 'ASSISTANT', 'That\'s great! Are you a student, or do you work at a school? What are you studying or teaching there?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('6c5744ac4948aadd2a37dff8034558a8', '70741bd4e7dccd293c48b6476efd752b', 'ASSISTANT', 'That\'s good to hear you\'re doing well! But I asked what you\'re doing, not how you\'re feeling. 😊 So, are you at home or out somewhere right now?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('6fcbbc781a18ec3fc21ce7839359bfe5', 'ac1c82f8446fa71d78813261a5536e51', 'ASSISTANT', 'Hey there! I\'m just hanging out and ready to chat with you! How about you? What have you been up to today?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('6ff9bc6a818e6c6191880b88ebb7759b', 'f0709f057d631ffb78947615bf59fa85', 'USER', 'The.', NULL, NULL, 4, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('713d41ccd2d219030b09a8ea04b82407', 'f0709f057d631ffb78947615bf59fa85', 'ASSISTANT', 'That\'s great! What are you studying? Are you in high school or college?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('72a9028f79aaf2d819faa15150aa8e92', '836ebc109f6bf770ccd8d5e278478a8f', 'ASSISTANT', 'I see you\'re talking about going to school! But I think you mean you went yesterday, right? Did you have any interesting classes?\n---\n💡 小提示：时间状语\"yesterday\"表示过去，需要配合过去时态哦。\n✅ 正确表达：I went to school yesterday.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('749462a4556ee100fe1317845eedc8e4', 'ae3482c6c3341f77227260c212a62960', 'ASSISTANT', 'Oh, I\'m not a real person, so I can\'t eat! But I appreciate you asking. 😊 How about you? Have you had any good meals recently?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('76fcc26fa4c01cd34bdf41cede7994a1', '30bf57737b0ec0c617446aaa2327eddf', 'ASSISTANT', 'It\'s a good question! But I think you meant to ask about my school, right? I don\'t have one since I\'m just a program, haha.\n\nBut tell me about yours! Where do you go to school?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('77c6e2904b14526beee88ab526a749d1', 'f0709f057d631ffb78947615bf59fa85', 'ASSISTANT', 'Hi there! How\'s it going today? I\'m glad you started with \"I\" — are you trying to say something about yourself? Go ahead and finish your sentence! 😊', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('78c00b9b87d0f9f6d3f5e3fa2fc7c09c', 'ae3482c6c3341f77227260c212a62960', 'USER', 'I am go to school yesterday.', NULL, NULL, NULL, 2, 1, NULL);
INSERT INTO `chat_messages` VALUES ('7b211e17ba98e954c7df3471b3eb7745', '728713856d37812198441282c97fc8a8', 'USER', 'I\'m going to school yesterday.', NULL, NULL, 71, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('7d77fb815723afc2823dffb5d31f51a8', '30bf57737b0ec0c617446aaa2327eddf', 'USER', 'The.', NULL, NULL, 0, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('7e5c493dfa8fc9586ddb2518567d24fb', '956a534fc962f5aed8051ffe05e5c672', 'ASSISTANT', 'That\'s great! Being a student is such an exciting time. What are you studying?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('800fa7ded91025aa5a3f4c07174cc5db', '59443ede71139fed669372621b5eb78d', 'USER', 'I\'m a student.', NULL, NULL, 97, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('822dc990cf6a01c89a83ffdf0e3caa0f', '2e1326fd7c4d43ffb857f11459ff4d8e', 'USER', 'I\'m going to school yesterday.', NULL, NULL, 83, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('83132e58440a19bed1ed1971374b8ecd', '836ebc109f6bf770ccd8d5e278478a8f', 'ASSISTANT', 'Right now, I\'m here chatting with you and helping you practice English! I\'m always ready to talk. So, what about you? What are you doing at the moment?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('837fd218e1df2ab09b473d64c832dfaa', '728713856d37812198441282c97fc8a8', 'ASSISTANT', 'Oh, it looks like you started to type something! 😊 What were you going to say? Take your time — no rush at all.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('8552c55be56663a162fc8bdf51362df8', '836ebc109f6bf770ccd8d5e278478a8f', 'USER', 'I am going to school yesterday.', NULL, NULL, 87, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('91c798044915048b624728fc3e9ff4ef', '2e1326fd7c4d43ffb857f11459ff4d8e', 'ASSISTANT', 'It seems like you\'re trying to start a new sentence with \"The\" again. That\'s okay! Let me help you. You could say something like:\n\n\"The teacher was very nice yesterday.\"\n\nOr: \"The weather was cold in the morning.\"\n\nTry finishing your sentence — I\'m listening! 😊', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('970ef79957f2ef469089e6f009377536', '4f65f097a39818b446485a6329d26964', 'ASSISTANT', 'Welcome to our restaurant! It looks like you\'re ready to order. Is there anything you\'d like to say? Take your time — I\'m here to help you practice. 😊', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('a2e6cacf446deab43b3b387f3eb5c0b6', '70741bd4e7dccd293c48b6476efd752b', 'USER', 'What are you doing now?', NULL, NULL, 94, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('a493c6a5a6021d9e9ca98e31c622cec6', 'ae3482c6c3341f77227260c212a62960', 'USER', 'Do you want to eat?', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('a6289903e8d9d5b06ae336bd8665a20d', '70741bd4e7dccd293c48b6476efd752b', 'ASSISTANT', 'It seems like you started a sentence but didn\'t finish it. No worries! 😊\n\nDid you mean to say something like \"I\'m a student\" or \"I work at...\"? Go ahead and give it another try!', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('a92c4e4e0684b28837e9a7d8892e5def', '70741bd4e7dccd293c48b6476efd752b', 'USER', 'What do you do?', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b2c8bc9c58ce468efe3aead25f462257', 'ae3482c6c3341f77227260c212a62960', 'ASSISTANT', 'That\'s great to hear! So, what topics would you like to talk about today? Maybe your weekend plans or something you\'re interested in?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b2cd5af8a90d21d537397609e84d4bac', 'ae3482c6c3341f77227260c212a62960', 'ASSISTANT', 'Ah, I see you\'re new to something! What are you new to? Is it trying new foods, cooking, or something else?\n---\n💡 小提示：\"New to it\" 有点不完整，可以说完整一些。\n✅ 正确表达：I\'m new to it. 或者更具体：I\'m new to cooking.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b3704863aa7c85b04ffd1dc07ce94c06', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'ASSISTANT', 'I\'m just relaxing at home right now, reading a book and enjoying some coffee. What about you? What have you been up to today?\n\n---\n💡 小提示：你说的 \"Do the\" 和 \"Love!\" 不太完整哦。如果你想表达 \"我喜欢\" 或 \"我正在做某事\"，可以说完整句子。\n✅ 正确表达：I love it! 或者 I\'m doing...', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b3fb8be155eeb38f5c58039422d43240', '30bf57737b0ec0c617446aaa2327eddf', 'ASSISTANT', 'Oh, you went to school yesterday! How was it?\n---\n💡 小提示：描述过去发生的事情要用过去时态哦。\n✅ 正确表达：I went to school yesterday.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b4a69b38912b5eba99b12fac8f58e6cc', '2e1326fd7c4d43ffb857f11459ff4d8e', 'USER', 'The.', NULL, NULL, 2, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b770ee0eaff8f2e9b8643e2941ecc6fc', '30bf57737b0ec0c617446aaa2327eddf', 'USER', 'Where is it?', NULL, NULL, 27, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b7b77943c94be82915321d878be9940b', 'f0709f057d631ffb78947615bf59fa85', 'USER', 'I am a student.', NULL, NULL, 96, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b7e35d8c8bff3ed46e5fc6fb9348003d', '59443ede71139fed669372621b5eb78d', 'USER', 'The.', NULL, NULL, 5, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b8651c54a02940476ed5dbb10aa46250', '836ebc109f6bf770ccd8d5e278478a8f', 'ASSISTANT', 'I\'m glad you like it! But let me help you with that sentence. \"I\'m very like this\" isn\'t quite right.\n---\n💡 小提示：\"like\"是动词，前面不需要\"am\"。\n✅ 正确表达：I like this very much. / I really like this.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b98eddc17926a555cfadf5b759583dee', '4f65f097a39818b446485a6329d26964', 'ASSISTANT', 'That\'s a great start! You\'re telling me what you\'re doing right now. 😊\n\nBut in this restaurant scenario, you\'re the customer and I\'m the server. So you could say something like:\n\n\"I\'d like to order some food.\"\n\nWant to try that? Or you can tell me what kind of food you\'re in the mood for today!', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('b9caae426b9e1e745605a7b033907d87', '836ebc109f6bf770ccd8d5e278478a8f', 'USER', 'I\'m very like this.', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('bcdb5cc887370fe32fbbe00aa766bc7c', '956a534fc962f5aed8051ffe05e5c672', 'USER', 'I am a student.', NULL, NULL, 98, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('bf11da62da0e847ec2b9711a9eacf41c', 'ae3482c6c3341f77227260c212a62960', 'USER', 'I will think.', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('bf1b00442a1d38054c1277aa9da59c74', '728713856d37812198441282c97fc8a8', 'USER', 'The.', NULL, NULL, 49, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('c085fa98ea8a196860916d9504364e2b', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'ASSISTANT', 'I\'m actually just relaxing and chatting with you right now! It\'s always nice to have a conversation. What about you? Do you have any fun plans for the rest of your day?\n\n---\n💡 小提示：你写的是 \"Do the.\"，这个句子不完整。如果你想问 \"你呢？\" 或者 \"你也是吗？\"，可以说：\n✅ 正确表达：What about you? / And you?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('c201f208a8bc441661bc95b92f6c1ad0', 'ae3482c6c3341f77227260c212a62960', 'USER', 'I think you\'re right.', NULL, NULL, 89, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('cb2c284c7fd708374c383417c00adb59', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'USER', 'The.', NULL, NULL, 2, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('cb6d5093397af8eafb3dd31bcba40657', '30bf57737b0ec0c617446aaa2327eddf', 'ASSISTANT', 'That\'s great! Going to school is important. What subjects are you studying these days?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('cdbf04c485c7c1627b34d975ef19ea29', '836ebc109f6bf770ccd8d5e278478a8f', 'USER', 'What are you doing now?', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('d0aa8590bd276e72c682294ce34e1d8b', '956a534fc962f5aed8051ffe05e5c672', 'ASSISTANT', 'Hmm, I think you were about to say something. Were you going to tell me what you\'re studying? Don\'t worry, just take your time and try again!', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('d0d1903043b29d49d876d4b25cd5163f', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'ASSISTANT', 'I\'m here chatting with you! Right now, I\'m just relaxing and enjoying this conversation. How about you? What are you up to today?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('d11c9c728cfc8f8780fb0eefe2fe5773', '2e1326fd7c4d43ffb857f11459ff4d8e', 'ASSISTANT', 'Oh, you went to school yesterday! How was it? Did you have any interesting classes?\n---\n💡 小提示：表示过去发生的事情要用过去时态哦。\n✅ 正确表达：I went to school yesterday.', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('d28761c83f6c74b7718e8725146895e6', '59443ede71139fed669372621b5eb78d', 'ASSISTANT', 'That\'s great! Being a student is an exciting time. What are you studying?', NULL, NULL, NULL, NULL, 0, NULL);
INSERT INTO `chat_messages` VALUES ('d4312ec76f6f72cf696d244e58f9c91e', 'a489c1707b5ebfc3d1bc2f936b2f07a4', 'USER', 'Do the.', NULL, NULL, 1, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('de112d31d39bf71b6160878b3b74c798', '30bf57737b0ec0c617446aaa2327eddf', 'USER', 'I am going to school.', NULL, NULL, 92, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('e632c15c8e173a26c229c7791a1993b0', '4f65f097a39818b446485a6329d26964', 'USER', 'The.', NULL, NULL, 0, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('e9ecaa43c1b04efcad088839306b69d1', '836ebc109f6bf770ccd8d5e278478a8f', 'USER', 'How old are you?', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('ec8dc0be1b028d3c2a1f0163469ac056', 'ac1c82f8446fa71d78813261a5536e51', 'USER', 'What are you doing now?', NULL, NULL, 98, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('edfe267b18dcee5ea1b582c96061515b', 'ae3482c6c3341f77227260c212a62960', 'USER', 'New to it.', NULL, NULL, 86, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('f95110885d494f284e97871b23bc277e', '70741bd4e7dccd293c48b6476efd752b', 'USER', 'I\'m fine, thank you.', NULL, NULL, NULL, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('fa36daed129b1d57a85bf5dfd8ce61c6', '30bf57737b0ec0c617446aaa2327eddf', 'USER', 'I\'m going to school yesterday.', NULL, NULL, 92, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('fd20945c3742497a9014bdaa1864bdd1', 'f0709f057d631ffb78947615bf59fa85', 'USER', 'I.', NULL, NULL, 39, 0, 0, NULL);
INSERT INTO `chat_messages` VALUES ('fe0ab3bed2da6e282eb48bdf8117f7bc', 'f0709f057d631ffb78947615bf59fa85', 'ASSISTANT', 'It looks like you started a new sentence with \"The\" — that\'s a good start! Could you finish your thought? For example: \"The school I go to is...\" or \"The subject I like is...\" Feel free to try!', NULL, NULL, NULL, NULL, 0, NULL);

-- ----------------------------
-- Table structure for chat_sessions
-- ----------------------------
DROP TABLE IF EXISTS `chat_sessions`;
CREATE TABLE `chat_sessions`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `scene_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '场景ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会话标题',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `overall_score` int NULL DEFAULT NULL COMMENT '综合评分',
  `pronunciation_score` int NULL DEFAULT NULL COMMENT '发音评分',
  `grammar_score` int NULL DEFAULT NULL COMMENT '语法评分',
  `vocabulary_score` int NULL DEFAULT NULL COMMENT '词汇评分',
  `fluency_score` int NULL DEFAULT NULL COMMENT '流利度评分',
  `interaction_score` int NULL DEFAULT NULL COMMENT '交互评分',
  `duration_seconds` int NULL DEFAULT NULL COMMENT '对话时长（秒）',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态 (ACTIVE/PAUSED/ENDED)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除 (0-否 1-是)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_scene_id`(`scene_id` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对话会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_sessions
-- ----------------------------
INSERT INTO `chat_sessions` VALUES ('00a630ecb75637356b59e87b09501b88', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 16:39:37', '2026-06-07 16:39:57', 98, 98, NULL, NULL, NULL, NULL, 20, 2, 'ENDED', NULL, '2026-06-07 20:47:34', 1);
INSERT INTO `chat_sessions` VALUES ('2e1326fd7c4d43ffb857f11459ff4d8e', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 19:39:00', '2026-06-07 19:39:36', 67, 45, 100, NULL, NULL, NULL, 35, 4, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('30bf57737b0ec0c617446aaa2327eddf', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 19:57:30', '2026-06-07 19:57:58', 82, 70, 100, NULL, NULL, NULL, 27, 8, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('4f65f097a39818b446485a6329d26964', 'ff096ae7d69e9fed7379848598961456', 'restaurant', '餐厅点餐对话', '2026-06-07 18:29:18', '2026-06-07 18:29:45', 42, 42, NULL, NULL, NULL, NULL, 27, 6, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('59443ede71139fed669372621b5eb78d', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 20:55:05', '2026-06-07 20:55:31', 71, 51, 100, NULL, NULL, NULL, 25, 4, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('70741bd4e7dccd293c48b6476efd752b', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 21:08:14', '2026-06-07 21:09:45', 96, 94, 100, NULL, NULL, NULL, 91, 10, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('728713856d37812198441282c97fc8a8', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 16:48:24', '2026-06-07 16:48:58', 70, 70, NULL, NULL, NULL, NULL, 34, 6, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('836ebc109f6bf770ccd8d5e278478a8f', '783b75dedfe483c4a28676932320f8bf', 'daily', '日常对话对话', '2026-06-07 23:29:13', '2026-06-07 23:30:58', 92, 87, 100, NULL, NULL, NULL, 105, 8, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('956a534fc962f5aed8051ffe05e5c672', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 20:58:05', '2026-06-07 20:58:42', 75, 59, 100, NULL, NULL, NULL, 37, 4, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('a489c1707b5ebfc3d1bc2f936b2f07a4', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 23:18:39', '2026-06-07 23:19:16', 44, 6, 100, NULL, NULL, NULL, 37, 8, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('ac1c82f8446fa71d78813261a5536e51', '783b75dedfe483c4a28676932320f8bf', 'daily', '日常对话对话', '2026-06-07 23:35:29', '2026-06-07 23:35:52', 99, 98, 100, NULL, NULL, NULL, 23, 2, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('ae3482c6c3341f77227260c212a62960', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 23:38:38', '2026-06-07 23:40:48', 92, 89, 97, NULL, NULL, NULL, 129, 14, 'ENDED', NULL, NULL, 0);
INSERT INTO `chat_sessions` VALUES ('f0709f057d631ffb78947615bf59fa85', 'ff096ae7d69e9fed7379848598961456', 'daily', '日常对话对话', '2026-06-07 19:27:38', '2026-06-07 19:27:56', 46, 46, NULL, NULL, NULL, NULL, 18, 6, 'ENDED', NULL, NULL, 0);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `native_language` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'zh-CN' COMMENT '母语',
  `english_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'B1' COMMENT '英语水平(A1-C2)',
  `learning_goal` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学习目标',
  `status` int NULL DEFAULT 1 COMMENT '状态(0-禁用 1-正常)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除(0-否 1-是)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('783b75dedfe483c4a28676932320f8bf', '19385125664', '$2a$10$nU/IxJHGl9WsoueXFYxeXefOwKzOkbjIMIufiLesayYAOd01qcqSe', '练习口语1', NULL, 'zh-CN', 'B1', '', 1, '2026-06-07 23:27:39', '2026-06-07 23:28:01', 0);
INSERT INTO `users` VALUES ('ff096ae7d69e9fed7379848598961456', '17885378825', '$2a$10$I3uPZ3kEruhNjf2K4bCPJOC.8TDjxq3NzbAZ9qnLZ8zv4JNyRF4fy', '练习口语第一天', NULL, 'en-US', 'B1', '学习流利交流', 1, '2026-06-07 15:52:58', '2026-06-07 22:59:29', 0);

-- ----------------------------
-- Table structure for vocabularies
-- ----------------------------
DROP TABLE IF EXISTS `vocabularies`;
CREATE TABLE `vocabularies`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '词汇ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单词',
  `phonetic_us` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '音标（美式）',
  `phonetic_uk` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '音标（英式）',
  `part_of_speech` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '词性',
  `meaning_cn` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文释义',
  `meaning_en` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '英文释义',
  `examples` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '例句（JSON数组）',
  `scene_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源场景ID',
  `chat_session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源对话ID',
  `difficulty` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '难度等级 (A1-C2)',
  `mastery_level` int NULL DEFAULT 0 COMMENT '掌握程度 (0-未学习 1-学习中 2-已掌握 3-需复习)',
  `learn_count` int NULL DEFAULT 0 COMMENT '学习次数',
  `review_count` int NULL DEFAULT 0 COMMENT '复习次数',
  `correct_count` int NULL DEFAULT 0 COMMENT '正确次数',
  `wrong_count` int NULL DEFAULT 0 COMMENT '错误次数',
  `last_learn_at` datetime NULL DEFAULT NULL COMMENT '最后学习时间',
  `next_review_at` datetime NULL DEFAULT NULL COMMENT '下次复习时间',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `status` int NULL DEFAULT 1 COMMENT '状态 (0-禁用 1-正常)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除 (0-否 1-是)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_word`(`word` ASC) USING BTREE,
  INDEX `idx_mastery_level`(`mastery_level` ASC) USING BTREE,
  INDEX `idx_next_review_at`(`next_review_at` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '词汇表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vocabularies
-- ----------------------------
INSERT INTO `vocabularies` VALUES ('12edd8e2491abded6cf67e20f4bbb83d', '783b75dedfe483c4a28676932320f8bf', 'going', '/ˈɡoʊɪŋ/', '/ˈɡəʊɪŋ/', 'n.', '进展；离开；状态', 'the act of leaving or the condition of something progressing', 'The going was slow on the muddy trail.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:29:27', '2026-06-10 23:29:27', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('139f11937d4202383430ca3654f88ed7', 'ff096ae7d69e9fed7379848598961456', 'new', '/nuː/', '/njuː/', 'adj.', '新的', 'not existing before; recently made, discovered, or experienced', 'I bought a new phone yesterday.', 'daily', NULL, 'B1', 3, 1, 0, 0, 1, '2026-06-07 23:39:18', '2026-06-08 23:39:18', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('13c2e1dd683a3cf068f65833acd44b46', 'ff096ae7d69e9fed7379848598961456', 'it', '/ɪt/', '/ɪt/', 'pron.', '它', 'used to refer to a thing, animal, or situation that has already been mentioned or is known about', 'I found a book on the table, and it was very interesting.', 'daily', NULL, 'B1', 1, 2, 0, 2, 0, '2026-06-07 23:39:38', '2026-06-10 23:39:21', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('14a737ec166ab708bbdcfe3b6369767b', 'ff096ae7d69e9fed7379848598961456', 'to', '/tuː/', '/tuː/', 'prep.', '向，朝；到，往；给，对于', 'used to show direction, purpose, or the person or thing that receives something', 'She gave the book to her friend.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:39:20', '2026-06-10 23:39:20', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('2330e8487710a7a4fdabee343f964b8d', 'ff096ae7d69e9fed7379848598961456', 'the', '/ðə/', '/ðə/', 'art.', '这，那（定冠词）', 'used to refer to a specific person or thing that is known to the listener or reader', 'The book on the table is mine.', 'daily', NULL, 'B1', 3, 3, 0, 0, 3, '2026-06-07 23:19:14', '2026-06-08 23:19:14', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('2386d9dda17b9b80cab32c8c09e5783b', '783b75dedfe483c4a28676932320f8bf', 'to', '/tuː/', '/tuː/', 'prep.', '向，朝；到，达；为了；给，对于', 'used to show direction, purpose, or the person or thing that receives something', 'She walked to the park every morning.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:29:29', '2026-06-10 23:29:29', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('25beb32dcf0c1d9ee2ebe998e68093e1', 'ff096ae7d69e9fed7379848598961456', 'do', '/duː/', '/duː/', 'v.', '做，执行', 'to perform an action or task', 'I need to do my homework before dinner.', 'daily', NULL, 'B1', 3, 2, 0, 1, 1, '2026-06-07 23:39:36', '2026-06-08 23:19:09', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('2c0af779b1e56c0ca67ba6e9bc9ad370', 'ff096ae7d69e9fed7379848598961456', 'right', '/raɪt/', '/raɪt/', 'adj.', '正确的；对的', 'correct or true; not wrong', 'She gave the right answer to the question.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:38:50', '2026-06-10 23:38:50', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('331d6931c3771047b40320a4df143e27', 'ff096ae7d69e9fed7379848598961456', 'you', '/juː/', '/juː/', 'pron.', '你；你们', 'used to refer to the person or people being spoken or written to', 'You should finish your homework before going out to play.', 'daily', NULL, 'B1', 1, 3, 0, 2, 1, '2026-06-07 23:39:36', '2026-06-10 21:08:28', '', 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('4a49944292afff4df0368051c002dc24', '783b75dedfe483c4a28676932320f8bf', 'am', '/æm/', '/æm/', 'v.', '是（be动词的第一人称单数现在式）', 'used as the first person singular present of \'be\'', 'I am a student.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:29:25', '2026-06-10 23:29:25', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('5fc99fe81190708b1e29d4a6d058179b', '783b75dedfe483c4a28676932320f8bf', 'doing', '/ˈduːɪŋ/', '/ˈduːɪŋ/', 'n.', '行为；活动；所作的事', 'the things that someone does or the activities that happen', 'His daily doing includes reading, writing, and exercising.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:35:44', '2026-06-10 23:35:44', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('73ee35811c3420b5aa388c650db27d99', 'ff096ae7d69e9fed7379848598961456', 'think', '/θɪŋk/', '/θɪŋk/', 'v.', '思考；认为', 'to have a belief or opinion about something, or to use your mind to consider ideas', 'I think it\'s going to rain tomorrow, so we should bring an umbrella.', 'daily', NULL, 'A1', 3, 1, 0, 0, 1, '2026-06-07 23:38:48', '2026-06-08 23:38:48', '', 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('8600fe6ceb008869ec3e5de1730bca0c', 'ff096ae7d69e9fed7379848598961456', 'vocabulary', '/vəˈkæbjəleri/', '/vəˈkæbjələri/', 'n.', '词汇；词汇量', 'all the words that a person knows or uses', 'Reading regularly is a great way to expand your vocabulary.', 'daily', NULL, 'A1', 0, 0, 0, 0, 0, NULL, NULL, '', 1, NULL, '2026-06-07 20:41:08', 1);
INSERT INTO `vocabularies` VALUES ('8f1197aeb890c5709d22d4166ad56e12', 'ff096ae7d69e9fed7379848598961456', 'now', '/naʊ/', '/naʊ/', 'adv.', '现在，此刻', 'at the present time or moment', 'I need to finish my homework now.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 21:08:32', '2026-06-10 21:08:32', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('8f7638f906e9842dd951cfc4d6f5c5b9', '783b75dedfe483c4a28676932320f8bf', 'you', '/juː/', '/juː/', 'pron.', '你；你们', 'the person or people being spoken or written to', 'You should always tell the truth.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:35:42', '2026-06-10 23:35:42', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('902bc78031a66631ece526c58b6949cb', 'ff096ae7d69e9fed7379848598961456', 'doing', '/ˈduːɪŋ/', '/ˈduːɪŋ/', 'n.', '行为；活动；所作之事', 'the things that someone does or the activities they are involved in', 'His daily doing includes reading, writing, and exercising.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 21:08:30', '2026-06-10 21:08:30', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('a1231e2b06986e89f52ee1016a4cff6f', 'ff096ae7d69e9fed7379848598961456', 'am', '/æm/', '/æm/', 'v.', '是（第一人称单数现在时）', 'used as the first person singular present tense of \'be\'', 'I am a student learning English.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 20:58:14', '2026-06-10 20:58:14', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('a3ca1f3fc9c89143a4d20dbf7e7d0694', '783b75dedfe483c4a28676932320f8bf', 'yesterday', '/ˈjɛstərˌdeɪ/', '/ˈjestədeɪ/', 'adv.', '昨天', 'on the day before today', 'I went to the park yesterday and had a great time.', 'daily', NULL, 'B1', 3, 1, 0, 0, 1, '2026-06-07 23:29:32', '2026-06-08 23:29:32', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('aa6040ac0de5022c399947596a1b6db0', '783b75dedfe483c4a28676932320f8bf', 'now', '/naʊ/', '/naʊ/', 'adv.', '现在，此刻', 'at the present time or moment', 'I need to finish this report now, so please don\'t disturb me.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:35:45', '2026-06-10 23:35:45', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('bea92a0610672f89544eac27cb73a2ee', '783b75dedfe483c4a28676932320f8bf', 'are', '/ɑr/', '/ɑː/', 'v.', '是（be的第二人称单数现在时和所有人称复数现在时）', 'used as the present tense form of \'be\' with \'you\', \'we\', \'they\', and plural nouns', 'You are my best friend.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:35:41', '2026-06-10 23:35:41', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('becdc2bc9b6b30496450e77bef02679a', 'ff096ae7d69e9fed7379848598961456', 'love', '/lʌv/', '/lʌv/', 'n.', '爱；热爱', 'a strong feeling of deep affection for someone or something', 'Her love for her family is the most important thing in her life.', 'daily', NULL, 'B1', 3, 1, 0, 0, 1, '2026-06-07 23:19:09', '2026-06-08 23:19:09', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('ca5c642ed72afcf7bab94e0a24434d87', 'ff096ae7d69e9fed7379848598961456', 'chew', '/tʃuː/', '/tʃuː/', 'v.', '咀嚼', 'to crush food with the teeth before swallowing', 'The baby likes to chew on a soft toy.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:39:38', '2026-06-10 23:39:38', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('cbd46ce2764ba227ce327668a1467e0f', '783b75dedfe483c4a28676932320f8bf', 'school', '/skuːl/', '/skuːl/', 'n.', '学校', 'a place where children go to learn', 'She walks to school every morning.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:29:30', '2026-06-10 23:29:30', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('ccc0af13df62d9b49537da29178ea5ea', '783b75dedfe483c4a28676932320f8bf', 'what', '/wʌt/', '/wɒt/', 'pron.', '什么', 'used to ask for information about something', 'What is your name?', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 23:35:39', '2026-06-10 23:35:39', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('e12ca94851548057c56fc8b19a5724e6', 'ff096ae7d69e9fed7379848598961456', 'students', '/ˈstuːdənts/', '/ˈstjuːdənts/', 'n.', '学生', 'a person who is studying at a school, college, or university', 'The students are preparing for their final exams in the library.', 'daily', NULL, 'A1', 0, 0, 0, 0, 0, NULL, NULL, '', 1, NULL, '2026-06-07 20:59:14', 1);
INSERT INTO `vocabularies` VALUES ('e88b04b95efe640dc04a7e2631e1fbca', 'ff096ae7d69e9fed7379848598961456', 'are', '/ɑr/', '/ɑː/', 'v.', '是（be的第二人称单数现在时和所有人称复数现在时）', 'the form of the verb \'be\' used with \'you\', \'we\', and \'they\' in the present tense', 'You are my best friend.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 21:08:27', '2026-06-10 21:08:27', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('e89639a950ba6e3be76b5bd7085df825', 'ff096ae7d69e9fed7379848598961456', 'what', '/wʌt/', '/wɒt/', 'pron.', '什么', 'used to ask for information about something', 'What is your favorite color?', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 21:08:25', '2026-06-10 21:08:25', NULL, 1, NULL, NULL, 0);
INSERT INTO `vocabularies` VALUES ('e9b4e1f2fb0e48fb51bc488bc48048ce', 'ff096ae7d69e9fed7379848598961456', 'student', '/ˈstuːdənt/', '/ˈstjuːdənt/', 'n.', '学生', 'a person who is studying at a school, college, or university', 'The student spent hours in the library preparing for the final exam.', 'daily', NULL, 'B1', 1, 1, 0, 1, 0, '2026-06-07 20:58:16', '2026-06-10 20:58:16', NULL, 1, NULL, NULL, 0);

-- ----------------------------
-- Table structure for vocabulary_learning_records
-- ----------------------------
DROP TABLE IF EXISTS `vocabulary_learning_records`;
CREATE TABLE `vocabulary_learning_records`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '记录ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `vocabulary_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '词汇ID',
  `learn_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学习类型 (LEARN-学习 REVIEW-复习 TEST-测试)',
  `learn_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学习方式 (READ-阅读 LISTEN-听力 SPEAK-口语 WRITE-拼写)',
  `is_correct` tinyint(1) NOT NULL COMMENT '是否正确 (true-正确 false-错误)',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户答案',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '正确答案',
  `score` int NULL DEFAULT NULL COMMENT '得分',
  `duration` int NULL DEFAULT NULL COMMENT '学习时长（秒）',
  `learn_at` datetime NOT NULL COMMENT '学习时间',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NULL DEFAULT 0 COMMENT '是否删除 (0-否 1-是)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_vocabulary_id`(`vocabulary_id` ASC) USING BTREE,
  INDEX `idx_learn_at`(`learn_at` ASC) USING BTREE,
  INDEX `idx_learn_type`(`learn_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '词汇学习记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vocabulary_learning_records
-- ----------------------------
INSERT INTO `vocabulary_learning_records` VALUES ('0e0174a10241839a26a374c96bcffb90', 'ff096ae7d69e9fed7379848598961456', 'ca5c642ed72afcf7bab94e0a24434d87', 'LEARN', 'SPEAK', 1, NULL, NULL, 86, 5, '2026-06-07 23:39:38', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('0e4dd107051c6065121682209ff063bf', '783b75dedfe483c4a28676932320f8bf', 'bea92a0610672f89544eac27cb73a2ee', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 23:35:41', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('168e45399f31d0c7c1ec26bef395a2e9', 'ff096ae7d69e9fed7379848598961456', '25beb32dcf0c1d9ee2ebe998e68093e1', 'LEARN', 'SPEAK', 0, NULL, NULL, 1, 5, '2026-06-07 23:19:09', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('18d8e5194d53f590739ae31eb657855a', 'ff096ae7d69e9fed7379848598961456', '14a737ec166ab708bbdcfe3b6369767b', 'LEARN', 'SPEAK', 1, NULL, NULL, 96, 5, '2026-06-07 23:39:20', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('1a9742ac982f9779c6245b45e2a1a142', 'ff096ae7d69e9fed7379848598961456', '331d6931c3771047b40320a4df143e27', 'LEARN', 'SPEAK', 1, NULL, NULL, 88, 5, '2026-06-07 23:39:36', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('2107393ed863d229e412b2a41b3a53e0', '783b75dedfe483c4a28676932320f8bf', 'cbd46ce2764ba227ce327668a1467e0f', 'LEARN', 'SPEAK', 1, NULL, NULL, 96, 5, '2026-06-07 23:29:30', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('2e81657c078c5909c6ce5ac365ad9505', 'ff096ae7d69e9fed7379848598961456', 'e89639a950ba6e3be76b5bd7085df825', 'LEARN', 'SPEAK', 1, NULL, NULL, 94, 5, '2026-06-07 21:08:25', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('2f16b8fd9e37503f8341f424b429db1d', 'ff096ae7d69e9fed7379848598961456', '13c2e1dd683a3cf068f65833acd44b46', 'LEARN', 'SPEAK', 1, NULL, NULL, 95, 5, '2026-06-07 23:39:38', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('31b5d22b19e73e22cf9a69640b4cabc3', 'ff096ae7d69e9fed7379848598961456', '2330e8487710a7a4fdabee343f964b8d', 'LEARN', 'SPEAK', 0, NULL, NULL, 19, 5, '2026-06-07 20:58:20', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('3995051b849ec5700ef8771b962c7b80', '783b75dedfe483c4a28676932320f8bf', '8f7638f906e9842dd951cfc4d6f5c5b9', 'LEARN', 'SPEAK', 1, NULL, NULL, 97, 5, '2026-06-07 23:35:42', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('3ae5bac5f29025033c69d8d0e1fa370e', 'ff096ae7d69e9fed7379848598961456', 'becdc2bc9b6b30496450e77bef02679a', 'LEARN', 'SPEAK', 0, NULL, NULL, 15, 5, '2026-06-07 23:19:09', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('52792c3e3925cb692fb38a6a34c666a2', 'ff096ae7d69e9fed7379848598961456', 'e88b04b95efe640dc04a7e2631e1fbca', 'LEARN', 'SPEAK', 1, NULL, NULL, 90, 5, '2026-06-07 21:08:27', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('53d86ddd24a04603e7fe7acc5bf27b7f', 'ff096ae7d69e9fed7379848598961456', '13c2e1dd683a3cf068f65833acd44b46', 'LEARN', 'SPEAK', 1, NULL, NULL, 96, 5, '2026-06-07 23:39:21', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('5f0acddfb84fb9153d6e6a5744305136', 'ff096ae7d69e9fed7379848598961456', '73ee35811c3420b5aa388c650db27d99', 'LEARN', 'SPEAK', 0, NULL, NULL, 78, 5, '2026-06-07 23:38:48', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('8a75b5df63bfc297f631b92a1740fac0', '783b75dedfe483c4a28676932320f8bf', 'aa6040ac0de5022c399947596a1b6db0', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 23:35:45', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('a0930dc3285acb9213beb0dd86add7b8', 'ff096ae7d69e9fed7379848598961456', '139f11937d4202383430ca3654f88ed7', 'LEARN', 'SPEAK', 0, NULL, NULL, 67, 5, '2026-06-07 23:39:18', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('a0f0266313a481757ba01b68aa0aeee3', '783b75dedfe483c4a28676932320f8bf', '2386d9dda17b9b80cab32c8c09e5783b', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 23:29:29', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('a175d559f5dfe5970f6d1a7684f228fb', '783b75dedfe483c4a28676932320f8bf', 'a3ca1f3fc9c89143a4d20dbf7e7d0694', 'LEARN', 'SPEAK', 0, NULL, NULL, 67, 5, '2026-06-07 23:29:32', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('b024c4281927a6c69a99785a0f9ae528', 'ff096ae7d69e9fed7379848598961456', '8f1197aeb890c5709d22d4166ad56e12', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 21:08:32', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('b583246847fc594d2c6b73199ab489fc', 'ff096ae7d69e9fed7379848598961456', '331d6931c3771047b40320a4df143e27', 'LEARN', 'SPEAK', 1, NULL, NULL, 85, 5, '2026-06-07 21:08:28', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('c3767501e8943d0e0477c188f7f93910', 'ff096ae7d69e9fed7379848598961456', '331d6931c3771047b40320a4df143e27', 'LEARN', 'SPEAK', 0, NULL, NULL, 44, 5, '2026-06-07 23:19:16', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('c47a77e2d55b0a9a9df514ed04852b11', 'ff096ae7d69e9fed7379848598961456', 'e9b4e1f2fb0e48fb51bc488bc48048ce', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 20:58:16', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('cceaf9147b07ce58fdab6f9a93a4d281', 'ff096ae7d69e9fed7379848598961456', '2330e8487710a7a4fdabee343f964b8d', 'LEARN', 'SPEAK', 0, NULL, NULL, 2, 5, '2026-06-07 23:19:14', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('cfab4c195dfe30e0cfd0ea39b423fa7a', '783b75dedfe483c4a28676932320f8bf', '5fc99fe81190708b1e29d4a6d058179b', 'LEARN', 'SPEAK', 1, NULL, NULL, 98, 5, '2026-06-07 23:35:44', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('d769ccdd22bd418f00d0957f50eb6ae6', 'ff096ae7d69e9fed7379848598961456', '25beb32dcf0c1d9ee2ebe998e68093e1', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 23:39:36', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('d8675eeaee527ddccca55ec0f33503f2', '783b75dedfe483c4a28676932320f8bf', '12edd8e2491abded6cf67e20f4bbb83d', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 23:29:27', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('dfac923ed5eb499f6e7dd8be8c87ed10', '783b75dedfe483c4a28676932320f8bf', 'ccc0af13df62d9b49537da29178ea5ea', 'LEARN', 'SPEAK', 1, NULL, NULL, 98, 5, '2026-06-07 23:35:39', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('e081f71648aefe73698bf69db6ca57f3', 'ff096ae7d69e9fed7379848598961456', '2c0af779b1e56c0ca67ba6e9bc9ad370', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 23:38:50', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('e8dea7d1c738e23b9dfc3beb5c6cf138', 'ff096ae7d69e9fed7379848598961456', '902bc78031a66631ece526c58b6949cb', 'LEARN', 'SPEAK', 1, NULL, NULL, 99, 5, '2026-06-07 21:08:30', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('ee5b65cc9a801c0e476983c4fdda2d77', '783b75dedfe483c4a28676932320f8bf', '4a49944292afff4df0368051c002dc24', 'LEARN', 'SPEAK', 1, NULL, NULL, 93, 5, '2026-06-07 23:29:25', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('eef141496d7343e767cbb519352b80ee', 'ff096ae7d69e9fed7379848598961456', 'a1231e2b06986e89f52ee1016a4cff6f', 'LEARN', 'SPEAK', 1, NULL, NULL, 97, 5, '2026-06-07 20:58:14', NULL, 0);
INSERT INTO `vocabulary_learning_records` VALUES ('efe72787b30d840217e4aaadfa000f3f', 'ff096ae7d69e9fed7379848598961456', '2330e8487710a7a4fdabee343f964b8d', 'LEARN', 'SPEAK', 0, NULL, NULL, 0, 5, '2026-06-07 23:19:09', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
