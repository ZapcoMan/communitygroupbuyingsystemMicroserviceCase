<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">社区团购系统后台管理</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            prefix-icon="User" 
            placeholder="请输入用户名"
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            prefix-icon="Lock" 
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  username: 'abo',
  password: 'abo'
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, message: '密码长度不能小于3位', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const result = await userStore.login(loginForm)
        
        if (result.success) {
          ElMessage.success('登录成功')
          router.push('/admin/home')
        } else {
          ElMessage.error(result.message || '登录失败')
        }
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error('登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
}

.login-form {
  margin-top: 20px;
}
</style>
