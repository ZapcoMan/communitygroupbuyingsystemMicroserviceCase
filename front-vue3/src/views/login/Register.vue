<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
        <el-form-item prop="zhanghao">
          <el-input 
            v-model="registerForm.zhanghao" 
            prefix-icon="User" 
            placeholder="请输入账号"
          ></el-input>
        </el-form-item>

        <el-form-item prop="mima">
          <el-input 
            v-model="registerForm.mima" 
            type="password" 
            prefix-icon="Lock" 
            placeholder="请输入密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            prefix-icon="Lock" 
            placeholder="请确认密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item prop="xingming">
          <el-input 
            v-model="registerForm.xingming" 
            prefix-icon="UserFilled" 
            placeholder="请输入姓名"
          ></el-input>
        </el-form-item>

        <el-form-item prop="xingbie">
          <el-radio-group v-model="registerForm.xingbie">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="shouji">
          <el-input 
            v-model="registerForm.shouji" 
            prefix-icon="Phone" 
            placeholder="请输入手机号"
          ></el-input>
        </el-form-item>

        <el-form-item prop="youxiang">
          <el-input 
            v-model="registerForm.youxiang" 
            prefix-icon="Message" 
            placeholder="请输入邮箱"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" style="width: 100%">
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="login-link">
            已有账号？<router-link to="/login">立即登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  zhanghao: '',
  mima: '',
  confirmPassword: '',
  xingming: '',
  xingbie: '男',
  shouji: '',
  youxiang: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.mima) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  zhanghao: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  mima: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  xingming: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  shouji: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  youxiang: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register(registerForm)
        if (res.code === 0) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(res.msg || '注册失败')
        }
      } catch (error) {
        console.error('注册错误:', error)
        ElMessage.error(error.message || '注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 450px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
}

.register-form {
  margin-top: 20px;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
