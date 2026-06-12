<template>
  <div class="user-center-page">
    <h2 class="page-title">个人中心</h2>
    
    <div class="user-center-content" v-loading="loading">
      <!-- 用户信息卡片 -->
      <el-card class="user-card">
        <div class="user-info">
          <img :src="userInfo.touxiang ? baseUrl + userInfo.touxiang : defaultAvatar" alt="头像" class="avatar">
          <div class="user-details">
            <h3 class="username">{{ userInfo.yonghuxingming || userInfo.yonghuzhanghao }}</h3>
            <p class="user-phone">{{ userInfo.lianxidianhua || '未绑定手机号' }}</p>
          </div>
          <el-button type="primary" @click="handleEditProfile">编辑资料</el-button>
        </div>
      </el-card>

      <!-- 功能菜单 -->
      <el-row :gutter="20" class="menu-grid">
        <el-col :span="8">
          <el-card class="menu-card" @click="goToOrders">
            <el-icon :size="40" color="#409eff"><Document /></el-icon>
            <p>我的订单</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="menu-card" @click="goToAddress">
            <el-icon :size="40" color="#67c23a"><Location /></el-icon>
            <p>收货地址</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="menu-card" @click="goToFavorites">
            <el-icon :size="40" color="#e6a23c"><Star /></el-icon>
            <p>我的收藏</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="menu-card" @click="handleChangePassword">
            <el-icon :size="40" color="#f56c6c"><Lock /></el-icon>
            <p>修改密码</p>
          </el-card>
        </el-col>
      </el-row>

      <!-- 最近订单 -->
      <el-card class="recent-orders">
        <template #header>
          <div class="card-header">
            <span>最近订单</span>
            <el-button type="text" @click="goToOrders">查看全部</el-button>
          </div>
        </template>
        
        <el-table :data="recentOrders" style="width: 100%">
          <el-table-column prop="orderno" label="订单号" width="180"></el-table-column>
          <el-table-column label="商品" min-width="200">
            <template #default="scope">
              <div class="order-goods">
                <img :src="baseUrl + scope.row.picture" alt="商品图片" class="goods-img">
                <span>{{ scope.row.goodname }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="total" label="金额" width="120">
            <template #default="scope">
              <span class="price">￥{{ scope.row.total }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <el-empty v-if="recentOrders.length === 0" description="暂无订单"></el-empty>
      </el-card>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑资料" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/user/file/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="editForm.touxiang" :src="baseUrl + editForm.touxiang" class="avatar">
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editForm.yonghuxingming"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.xingbie">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="editForm.lianxidianhua"></el-input>
        </el-form-item>
        <el-form-item label="身份证">
          <el-input v-model="editForm.shenfenzheng"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePassword">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user'
import { getOrderList } from '@/api/product'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const baseUrl = ref('/api/user/file/')
const uploadHeaders = ref({ Token: localStorage.getItem('token') || '' })
const defaultAvatar = ref('https://via.placeholder.com/100')
const loading = ref(false)
const recentOrders = ref([])

// 编辑资料
const editDialogVisible = ref(false)
const editForm = reactive({
  id: '',
  yonghuzhanghao: '',
  yonghuxingming: '',
  xingbie: '',
  lianxidianhua: '',
  shenfenzheng: '',
  touxiang: ''
})

// 修改密码
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 用户信息
const userInfo = ref({})

// 获取用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const userId = localStorage.getItem('userid')
    if (!userId) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    
    const res = await getUserInfo(userId)
    if (res.code === 0) {
      userInfo.value = res.data
      // 填充编辑表单
      Object.assign(editForm, res.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取最近订单
const fetchRecentOrders = async () => {
  try {
    const res = await getOrderList({
      page: 1,
      limit: 5,
      sort: 'addtime',
      order: 'desc'
    })
    if (res.code === 0) {
      recentOrders.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取订单失败:', error)
  }
}

// 编辑资料
const handleEditProfile = () => {
  editDialogVisible.value = true
}

// 头像上传成功
const handleAvatarSuccess = (res) => {
  if (res.code === 0) {
    editForm.touxiang = res.file
    ElMessage.success('上传成功')
  }
}

// 保存资料
const handleSaveProfile = async () => {
  try {
    const res = await updateUserInfo(editForm)
    if (res.code === 0) {
      ElMessage.success('保存成功')
      editDialogVisible.value = false
      fetchUserInfo() // 刷新用户信息
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 修改密码
const handleChangePassword = () => {
  passwordDialogVisible.value = true
}

// 保存密码
const handleSavePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await changePassword({
          password: passwordForm.oldPassword,
          newpassword: passwordForm.newPassword
        })
        
        if (res.code === 0) {
          ElMessage.success('密码修改成功')
          passwordDialogVisible.value = false
          // 清空表单
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败')
      }
    }
  })
}

// 跳转方法
const goToOrders = () => {
  router.push('/shop-order/list')
}

const goToAddress = () => {
  router.push('/shop-address/list')
}

const goToFavorites = () => {
  router.push('/storeup/list')
}

// 订单状态
const getStatusType = (status) => {
  const map = {
    '已支付': 'success',
    '未支付': 'warning',
    '已取消': 'info',
    '已完成': ''
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  return status || '未知'
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  fetchUserInfo()
  fetchRecentOrders()
})
</script>

<style scoped>
.user-center-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

/* 用户信息卡片 */
.user-card {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 20px;
  margin-bottom: 5px;
  color: #333;
}

.user-phone {
  font-size: 14px;
  color: #999;
}

/* 功能菜单 */
.menu-grid {
  margin-bottom: 20px;
}

.menu-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.menu-card p {
  margin-top: 10px;
  font-size: 14px;
  color: #333;
}

/* 最近订单 */
.recent-orders {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-goods {
  display: flex;
  align-items: center;
  gap: 10px;
}

.goods-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

/* 头像上传 */
.avatar-uploader {
  text-align: center;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
}
</style>
