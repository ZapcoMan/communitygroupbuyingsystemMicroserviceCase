<template>
  <div class="address-list-page">
    <div class="page-header">
      <h2 class="page-title">收货地址</h2>
      <el-button type="primary" @click="handleAddAddress">添加新地址</el-button>
    </div>
    
    <!-- 地址列表 -->
    <div v-loading="loading">
      <div v-for="address in addressList" :key="address.id" class="address-card">
        <div class="address-info">
          <div class="address-top">
            <span class="address-name">{{ address.name }}</span>
            <span class="address-phone">{{ address.phone }}</span>
            <el-tag v-if="address.isdefault === '是'" type="success" size="small">默认</el-tag>
          </div>
          <div class="address-detail">
            {{ address.region }}{{ address.detail }}
          </div>
        </div>
        <div class="address-actions">
          <el-button type="text" @click="handleEditAddress(address)">编辑</el-button>
          <el-button type="text" @click="handleSetDefault(address)" v-if="address.isdefault !== '是'">设为默认</el-button>
          <el-button type="text" style="color: #f56c6c;" @click="handleDeleteAddress(address.id)">删除</el-button>
        </div>
      </div>

      <el-empty v-if="!loading && addressList.length === 0" description="暂无收货地址">
        <el-button type="primary" @click="handleAddAddress">添加地址</el-button>
      </el-empty>
    </div>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '添加地址'" width="500px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="80px">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="addressForm.name"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-input v-model="addressForm.region" placeholder="例如: 北京市朝阳区"></el-input>
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="addressForm.detail" type="textarea" :rows="2"></el-input>
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="addressForm.isdefault" active-value="是" inactive-value="否"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const addressList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const addressFormRef = ref(null)

const addressForm = ref({
  id: '',
  name: '',
  phone: '',
  region: '',
  detail: '',
  isdefault: '否'
})

const addressRules = {
  name: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请输入地区', trigger: 'blur' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 获取地址列表
const fetchAddressList = async () => {
  loading.value = true
  try {
    const res = await getAddressList({
      page: 1,
      limit: 100
    })
    if (res.code === 0) {
      addressList.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取地址失败:', error)
    ElMessage.error('获取地址失败')
  } finally {
    loading.value = false
  }
}

// 添加地址
const handleAddAddress = () => {
  isEdit.value = false
  addressForm.value = {
    id: '',
    name: '',
    phone: '',
    region: '',
    detail: '',
    isdefault: '否'
  }
  dialogVisible.value = true
}

// 编辑地址
const handleEditAddress = (address) => {
  isEdit.value = true
  addressForm.value = { ...address }
  dialogVisible.value = true
}

// 保存地址
const handleSaveAddress = async () => {
  if (!addressFormRef.value) return
  
  await addressFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateAddress(addressForm.value)
        } else {
          res = await addAddress(addressForm.value)
        }
        
        if (res.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchAddressList()
        }
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      }
    }
  })
}

// 设为默认
const handleSetDefault = async (address) => {
  try {
    const res = await setDefaultAddress(address.id)
    if (res.code === 0) {
      ElMessage.success('设置成功')
      fetchAddressList()
    }
  } catch (error) {
    console.error('设置失败:', error)
    ElMessage.error('设置失败')
  }
}

// 删除地址
const handleDeleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗?', '提示', {
      type: 'warning'
    })
    
    const res = await deleteAddress(id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      fetchAddressList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  fetchAddressList()
})
</script>

<style scoped>
.address-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  color: #333;
}

/* 地址卡片 */
.address-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s;
}

.address-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.address-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.address-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.address-phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  font-size: 14px;
  color: #999;
}

.address-actions {
  display: flex;
  gap: 10px;
}
</style>
