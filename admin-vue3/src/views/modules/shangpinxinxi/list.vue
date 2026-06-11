<template>
  <div class="list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.shangpinmingcheng" placeholder="请输入商品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="商品类型">
          <el-select v-model="searchForm.shangpinleixing" placeholder="请选择商品类型" clearable>
            <el-option v-for="item in typeList" :key="item.id" :label="item.shangpinleixing" :value="item.shangpinleixing"></el-option>
          </el-select>
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
        <el-table-column prop="shangpinmingcheng" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="shangpinleixing" label="商品类型" width="120"></el-table-column>
        <el-table-column prop="tupian" label="商品图片" width="100">
          <template #default="scope">
            <img v-if="scope.row.tupian" :src="baseUrl + scope.row.tupian.split(',')[0]" class="table-img">
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">￥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="alllimittimes" label="库存" width="80"></el-table-column>
        <el-table-column prop="sold" label="销量" width="80"></el-table-column>
        <el-table-column prop="shangpinjianjie" label="简介" show-overflow-tooltip></el-table-column>
        <el-table-column prop="addtime" label="创建时间" width="180"></el-table-column>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="shangpinmingcheng">
              <el-input v-model="form.shangpinmingcheng"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品类型" prop="shangpinleixing">
              <el-select v-model="form.shangpinleixing" placeholder="请选择">
                <el-option v-for="item in typeList" :key="item.id" :label="item.shangpinleixing" :value="item.shangpinleixing"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input v-model="form.price" type="number"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存">
              <el-input v-model="form.alllimittimes" type="number"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品图片">
          <el-upload
            action="/springboot2c1hu/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            list-type="picture-card"
          >
            <img v-if="form.tupian" :src="baseUrl + form.tupian" class="upload-img">
            <el-icon v-else><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品简介">
          <el-input v-model="form.shangpinjianjie" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input v-model="form.shangpinxiangqing" type="textarea" :rows="5"></el-input>
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

const baseUrl = ref('http://localhost:8080/springboot2c1hu/file/')
const uploadHeaders = ref({ Token: localStorage.getItem('adminToken') || '' })
const loading = ref(false)
const tableData = ref([])
const typeList = ref([])
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
  shangpinmingcheng: '',
  shangpinleixing: ''
})

// 添加/编辑表单
const form = reactive({
  id: '',
  shangpinmingcheng: '',
  shangpinleixing: '',
  tupian: '',
  price: '',
  alllimittimes: '',
  sold: 0,
  shangpinjianjie: '',
  shangpinxiangqing: ''
})

// 表单验证规则
const rules = {
  shangpinmingcheng: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  shangpinleixing: [
    { required: true, message: '请选择商品类型', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ]
}

// 获取商品类型列表
const fetchTypeList = async () => {
  try {
    const res = await request.get('/shangpinleixing/list', { params: { page: 1, limit: 100 } })
    if (res.code === 0) {
      typeList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取类型列表失败:', error)
  }
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
    
    if (searchForm.shangpinmingcheng) {
      params.shangpinmingcheng = searchForm.shangpinmingcheng
    }
    if (searchForm.shangpinleixing) {
      params.shangpinleixing = searchForm.shangpinleixing
    }
    
    const res = await request.get('/shangpinxinxi/list', { params })
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
  searchForm.shangpinmingcheng = ''
  searchForm.shangpinleixing = ''
  handleSearch()
}

// 添加
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加商品'
  Object.assign(form, {
    id: '',
    shangpinmingcheng: '',
    shangpinleixing: '',
    tupian: '',
    price: '',
    alllimittimes: '',
    sold: 0,
    shangpinjianjie: '',
    shangpinxiangqing: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑商品'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗?', '提示', { type: 'warning' })
    const res = await request.post('/shangpinxinxi/delete', [id])
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
    await ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条数据吗?`, '提示', { type: 'warning' })
    const ids = multipleSelection.value.map(item => item.id)
    const res = await request.post('/shangpinxinxi/delete', ids)
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

// 图片上传成功
const handleUploadSuccess = (res) => {
  if (res.code === 0) {
    form.tupian = res.file
    ElMessage.success('上传成功')
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

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = isEdit.value ? '/shangpinxinxi/update' : '/shangpinxinxi/save'
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
  fetchTypeList()
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
