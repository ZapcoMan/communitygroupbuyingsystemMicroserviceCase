<template>
  <div class="list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.yonghuzhanghao" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.yonghuxingming" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>添加
        </el-button>
        <el-button type="danger" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table 
        :data="tableData" 
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="yonghuzhanghao" label="用户账号" width="120"></el-table-column>
        <el-table-column prop="yonghuxingming" label="用户姓名" width="120"></el-table-column>
        <el-table-column prop="xingbie" label="性别" width="80"></el-table-column>
        <el-table-column prop="lianxidianhua" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="shenfenzheng" label="身份证" width="180"></el-table-column>
        <el-table-column prop="touxiang" label="头像" width="100">
          <template #default="scope">
            <img v-if="scope.row.touxiang" :src="baseUrl + scope.row.touxiang" class="avatar-img">
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="注册时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #f56c6c;" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户账号" prop="yonghuzhanghao">
          <el-input v-model="form.yonghuzhanghao" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="mima" v-if="!isEdit">
          <el-input v-model="form.mima" type="password"></el-input>
        </el-form-item>
        <el-form-item label="用户姓名" prop="yonghuxingming">
          <el-input v-model="form.yonghuxingming"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.xingbie">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.lianxidianhua"></el-input>
        </el-form-item>
        <el-form-item label="身份证">
          <el-input v-model="form.shenfenzheng"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/user/file/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="form.touxiang" :src="baseUrl + form.touxiang" class="avatar">
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const baseUrl = ref('/api/user/file/')
const uploadHeaders = ref({ Token: localStorage.getItem('adminToken') || '' })
const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

// 搜索表单
const searchForm = reactive({
  yonghuzhanghao: '',
  yonghuxingming: ''
})

// 添加/编辑表单
const form = reactive({
  id: '',
  yonghuzhanghao: '',
  mima: '',
  yonghuxingming: '',
  xingbie: '男',
  lianxidianhua: '',
  shenfenzheng: '',
  touxiang: ''
})

// 表单验证规则
const rules = {
  yonghuzhanghao: [
    { required: true, message: '请输入用户账号', trigger: 'blur' }
  ],
  mima: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  yonghuxingming: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' }
  ]
}

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc'
    }
    
    if (searchForm.yonghuzhanghao) {
      params.yonghuzhanghao = searchForm.yonghuzhanghao
    }
    if (searchForm.yonghuxingming) {
      params.yonghuxingming = searchForm.yonghuxingming
    }
    
    const res = await request.get('/user/yonghu/list', { params })
    if (res.code === 0) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.yonghuzhanghao = ''
  searchForm.yonghuxingming = ''
  handleSearch()
}

// 添加
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加用户'
  Object.assign(form, {
    id: '',
    yonghuzhanghao: '',
    mima: '',
    yonghuxingming: '',
    xingbie: '男',
    lianxidianhua: '',
    shenfenzheng: '',
    touxiang: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗?', '提示', {
      type: 'warning'
    })
    
    const res = await request.post('/user/yonghu/delete', [id])
    if (res.code === 0) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的数据')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条数据吗?`, '提示', {
      type: 'warning'
    })
    
    const ids = multipleSelection.value.map(item => item.id)
    const res = await request.post('/user/yonghu/delete', ids)
    if (res.code === 0) {
      ElMessage.success('批量删除成功')
      multipleSelection.value = []
      fetchData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

// 选择变化
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
}

// 头像上传成功
const handleAvatarSuccess = (res) => {
  if (res.code === 0) {
    form.touxiang = res.file
    ElMessage.success('上传成功')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = isEdit.value ? '/user/yonghu/update' : '/user/yonghu/save'
        const res = await request.post(url, form)
        
        if (res.code === 0) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchData()
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.list-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.avatar-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
}
</style>
