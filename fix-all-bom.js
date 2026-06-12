const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const repoRoot = 'C:/Users/Administrator/Desktop/communitygroupbuyingsystemMicroserviceCase';
const projectDir = path.join(repoRoot, 'community-group-buying-microservices');

const bomFiles = [
    'cgb-product-service/src/main/java/com/cgb/product/controller/ProductCategoryController.java',
    'cgb-product-service/src/main/java/com/cgb/product/controller/ProductCollectionController.java',
    'cgb-product-service/src/main/java/com/cgb/product/controller/ProductCommentController.java',
    'cgb-product-service/src/main/java/com/cgb/product/controller/ProductController.java',
    'cgb-product-service/src/main/java/com/cgb/product/controller/ProductInquiryController.java',
    'cgb-product-service/src/main/java/com/cgb/product/dao/ProductCategoryDao.java',
    'cgb-product-service/src/main/java/com/cgb/product/dao/ProductCollectionDao.java',
    'cgb-product-service/src/main/java/com/cgb/product/dao/ProductCommentDao.java',
    'cgb-product-service/src/main/java/com/cgb/product/dao/ProductDao.java',
    'cgb-product-service/src/main/java/com/cgb/product/dao/ProductInquiryDao.java',
    'cgb-product-service/src/main/java/com/cgb/product/entity/ProductCategoryEntity.java',
    'cgb-product-service/src/main/java/com/cgb/product/entity/ProductCollectionEntity.java',
    'cgb-product-service/src/main/java/com/cgb/product/entity/ProductCommentEntity.java',
    'cgb-product-service/src/main/java/com/cgb/product/entity/ProductEntity.java',
    'cgb-product-service/src/main/java/com/cgb/product/entity/ProductInquiryEntity.java',
    'cgb-product-service/src/main/java/com/cgb/product/mq/ProductOrderMessageConsumer.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/ProductCategoryService.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/ProductCollectionService.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/ProductCommentService.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/ProductInquiryService.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/ProductService.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/impl/ProductCategoryServiceImpl.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/impl/ProductCollectionServiceImpl.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/impl/ProductCommentServiceImpl.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/impl/ProductInquiryServiceImpl.java',
    'cgb-product-service/src/main/java/com/cgb/product/service/impl/ProductServiceImpl.java',
    'cgb-product-service/src/test/java/com/cgb/product/service/impl/ProductCollectionServiceImplTest.java',
    'cgb-product-service/src/test/java/com/cgb/product/service/impl/ProductCommentServiceImplTest.java',
    'cgb-product-service/src/test/java/com/cgb/product/service/impl/ProductInquiryServiceImplTest.java',
    'cgb-product-service/src/test/java/com/cgb/product/service/impl/ProductServiceImplTest.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/controller/GroupBuyCommentController.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/controller/GroupBuyController.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/controller/GroupSlotController.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/dao/GroupBuyCommentDao.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/dao/GroupBuyDao.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/dao/GroupSlotDao.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/entity/GroupBuyCommentEntity.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/entity/GroupBuyEntity.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/entity/GroupSlotEntity.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/mq/GroupBuyStatusConsumer.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/service/GroupBuyCommentService.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/service/GroupBuyService.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/service/GroupSlotService.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/service/impl/GroupBuyCommentServiceImpl.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/service/impl/GroupBuyServiceImpl.java',
    'cgb-groupbuy-service/src/main/java/com/cgb/groupbuy/service/impl/GroupSlotServiceImpl.java',
    'cgb-groupbuy-service/src/test/java/com/cgb/groupbuy/service/impl/GroupBuyServiceImplTest.java',
    'cgb-groupbuy-service/src/test/java/com/cgb/groupbuy/service/impl/GroupSlotServiceImplTest.java',
    'cgb-content-service/src/main/java/com/cgb/content/controller/InformationController.java',
    'cgb-content-service/src/main/java/com/cgb/content/controller/MessageBoardController.java',
    'cgb-content-service/src/main/java/com/cgb/content/dao/InformationDao.java',
    'cgb-content-service/src/main/java/com/cgb/content/dao/MessageBoardDao.java',
    'cgb-content-service/src/main/java/com/cgb/content/entity/InformationEntity.java',
    'cgb-content-service/src/main/java/com/cgb/content/entity/MessageBoardEntity.java',
    'cgb-content-service/src/main/java/com/cgb/content/service/InformationService.java',
    'cgb-content-service/src/main/java/com/cgb/content/service/MessageBoardService.java',
    'cgb-content-service/src/main/java/com/cgb/content/service/impl/InformationServiceImpl.java',
    'cgb-content-service/src/main/java/com/cgb/content/service/impl/MessageBoardServiceImpl.java',
    'cgb-content-service/src/test/java/com/cgb/content/service/impl/InformationServiceImplTest.java',
    'cgb-content-service/src/test/java/com/cgb/content/service/impl/MessageBoardServiceImplTest.java',
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

let fixed = 0, skipped = 0, errors = 0;

for (const relPath of bomFiles) {
    const fullPath = path.join(projectDir, relPath);
    const gitPath = `HEAD:community-group-buying-microservices/${relPath}`;
    
    try {
        // Get clean content from git
        let gitContent = execSync(`git cat-file -p ${gitPath}`, { 
            cwd: repoRoot, 
            encoding: null 
        });
        
        // Strip BOM if present
        if (gitContent[0] === 0xEF && gitContent[1] === 0xBB && gitContent[2] === 0xBF) {
            gitContent = gitContent.slice(3);
        }
        
        // Write without BOM (fs.writeFileSync never adds BOM)
        fs.writeFileSync(fullPath, gitContent);
        
        // Verify no BOM
        const verify = fs.readFileSync(fullPath);
        if (verify[0] === 0xEF && verify[1] === 0xBB && verify[2] === 0xBF) {
            console.log('STILL BOM:', relPath);
        } else {
            console.log('FIXED:', relPath);
            fixed++;
        }
    } catch(e) {
        console.log('ERROR:', relPath, e.message);
        errors++;
    }
}

console.log(`\nDone: ${fixed} fixed, ${errors} errors`);
