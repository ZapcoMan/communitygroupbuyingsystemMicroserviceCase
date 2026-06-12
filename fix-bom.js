const fs = require('fs');
const path = require('path');

const root = 'C:/Users/Administrator/Desktop/communitygroupbuyingsystemMicroserviceCase/community-group-buying-microservices';

const bomFiles = [
    'cgb-user-service/src/main/java/com/cgb/user/controller/MemberController.java',
    'cgb-user-service/src/main/java/com/cgb/user/dao/MemberDao.java',
    'cgb-user-service/src/main/java/com/cgb/user/entity/MemberEntity.java',
    'cgb-user-service/src/main/java/com/cgb/user/entity/vo/MemberVO.java',
    'cgb-user-service/src/main/java/com/cgb/user/mq/UserOrderMessageConsumer.java',
    'cgb-user-service/src/main/java/com/cgb/user/service/MemberService.java',
    'cgb-user-service/src/main/java/com/cgb/user/service/impl/MemberServiceImpl.java',
    'cgb-user-service/src/test/java/com/cgb/user/service/impl/MemberServiceImplTest.java',
    'cgb-user-service/src/main/java/com/cgb/user/config/MybatisPlusConfig.java',
    'cgb-common/src/main/java/com/cgb/common/mq/GroupBuyMessage.java',
    'cgb-common/src/main/java/com/cgb/common/mq/MQTopics.java',
];

for (const relPath of bomFiles) {
    const fullPath = path.join(root, relPath);
    if (!fs.existsSync(fullPath)) {
        console.log('MISSING:', fullPath);
        continue;
    }
    const buf = fs.readFileSync(fullPath);
    if (buf[0] === 0xEF && buf[1] === 0xBB && buf[2] === 0xBF) {
        const clean = buf.slice(3);
        fs.writeFileSync(fullPath, clean);
        console.log('BOM removed:', relPath);
    } else {
        console.log('No BOM:', relPath, buf[0], buf[1], buf[2]);
    }
}

console.log('\n=== Verification ===');
for (const relPath of bomFiles) {
    const fullPath = path.join(root, relPath);
    if (fs.existsSync(fullPath)) {
        const buf = fs.readFileSync(fullPath);
        const hasBom = buf[0] === 0xEF && buf[1] === 0xBB && buf[2] === 0xBF;
        if (hasBom) console.log('STILL BOM:', relPath);
        else console.log('CLEAN:', relPath);
    }
}
