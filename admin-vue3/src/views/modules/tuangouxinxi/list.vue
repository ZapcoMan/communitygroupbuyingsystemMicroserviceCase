<template>
  <div class="list-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="团购名称">
          <el-input v-model="searchForm.tuangoumingcheng" placeholder="请输入团购名称" clearable></el-input>
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

    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>添加
        </el-button>
        <el-button type="danger" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>

      <el-table 
        :data="tableData" 
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="tuangoumingcheng" label="团购名称" width="150"></el-table-column>
        <el-table-column prop="tupian" label="团购图片" width="100">
          <template #default="scope">
            <img v-if="scope.row.tupian" :src="baseUrl + scope.row.tupian.split(',')[0]" class="table-img">
          </template>
        </el-table-column>
        <el-table-column prop="tuangoujiage" label="团购价格" width="100">
          <template #default="scope">￥{{ scope.row.tuangoujiage }}</template>
        </el-table-column>
        <el-table-column prop="shangpinjiage" label="原价" width="100">
          <template #default="scope">￥{{ scope.row.shangpinjiage }}</template>
        </el-table-column>
        <el-table-column prop="tuangourenshu" label="成团人数" width="100"></el-table-column>
        <el-table-column prop="alllimittimes" label="库存" width="80"></el-table-column>
        <el-table-column prop="sold" label="已售" width="80"></el-table-column>
        <el-table-column prop="jiezhishijian" label="截止时间" width="180"></el-table-column>
        <el-table-column prop="addtime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #f56c6c;" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="团购名称" prop="tuangoumingcheng">
              <el-input v-model="form.tuangoumingcheng"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="团购价格" prop="tuangoujiage">
              <el-input v-model="form.tuangoujiage" type="number"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原价" prop="shangpinjiage">
              <el-input v-model="form.shangpinjiage" type="number"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成团人数" prop="tuangourenshu">
              <el-input v-model="form.tuangourenshu" type="number"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存">
              <el-input v-model="form.alllimittimes" type="number"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间">
              <el-date-picker
                v-model="form.jiezhishijian"
                type="datetime"
                placeholder="选择日期时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="团购图片">
          <el-upload
            action="/api/groupbuy/file/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            list-type="picture-card"
          >
            <img v-if="form.tupian" :src="baseUrl + form.tupian" class="upload-img">
            <el-icon v-else><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="团购简介">
          <el-input v-model="form.tuangoujianjie" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="团购详情">
          <el-input v-model="form.tuangouxingqing" type="textarea" :rows="5"></el-input>
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

const baseUrl = ref('/api/groupbuy/file/')
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

const searchForm = reactive({
  tuangoumingcheng: ''
})

const form = reactive({
  id: '',
  tuangoumingcheng: '',
  tupian: '',
  tuangoujiage: '',
  shangpinjiage: '',
  tuangourenshu: '',
  alllimittimes: '',
  sold: 0,
  jiezhishijian: '',
  tuangoujianjie: '',
  tuangouxingqing: ''
})

const rules = {
  tuangoumingcheng: [
    { required: true, message: '请输入团购名称', trigger: 'blur' }
  ],
  tuangoujiage: [
    { required: true, message: '请输入团购价格', trigger: 'blur' }
  ],
  shangpinjiage: [
    { required: true, message: '请输入原价', trigger: 'blur' }
  ],
  tuangourenshu: [
    { required: true, message: '请输入成团人数', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc'
    }
    
    if (searchForm.tuangoumingcheng) {
      params.tuangoumingcheng = searchForm.tuangoumingcheng
    }
    
    const res = await request.get('/tuangouxinxi/list', { params })
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

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.tuangoumingcheng = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加团购'
  Object.assign(form, {
    id: '',
    tuangoumingcheng: '',
    tupian: '',
    tuangoujiage: '',
    shangpinjiage: '',
    tuangourenshu: '',
    alllimittimes: '',
    sold: 0,
    jiezhishijian: '',
    tuangoujianjie: '',
    tuangouxingqing: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑团购'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗?', '提示', { type: 'warning' })
    const res = await request.post('/tuangouxinxi/delete', [id])
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

const handleBatchDelete = async () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的数据')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条数据吗?`, '提示', { type: 'warning' })
    const ids = multipleSelection.value.map(item => item.id)
    const res = await request.post('/tuangouxinxi/delete', ids)
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

const handleUploadSuccess = (res) => {
  if (res.code === 0) {
    form.tupian = res.file
    ElMessage.success('上传成功')
  }
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = isEdit.value ? '/tuangouxinxi/update' : '/tuangouxinxi/save'
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

.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.table-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.upload-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
