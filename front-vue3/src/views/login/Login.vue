<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">社区团购系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <!-- 角色选择 -->
        <el-form-item prop="table">
          <el-select v-model="loginForm.table" placeholder="请选择登录角色" style="width: 100%">
            <el-option label="管理员" value="users"></el-option>
            <el-option label="用户" value="yonghu"></el-option>
          </el-select>
        </el-form-item>

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

        <!-- 注册链接 -->
        <el-form-item v-if="loginForm.table === 'yonghu'">
          <div class="register-link">
            还没有账号？<router-link to="/register">立即注册</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  table: 'yonghu',
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  table: [
    { required: true, message: '请选择登录角色', trigger: 'change' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
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
          
          // 跳转到首页或之前访问的页面
          const redirect = route.query.redirect || '/home'
          router.push(redirect)
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

// 忘记密码
const handleForgotPassword = () => {
  ElMessage.info('请联系管理员重置密码')
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

.register-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
