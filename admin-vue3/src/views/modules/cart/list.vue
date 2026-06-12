<template>
  <div class="list-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.goodname" placeholder="请输入商品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userid" placeholder="请输入用户ID" clearable></el-input>
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
        <el-table-column prop="goodname" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="picture" label="商品图片" width="100">
          <template #default="scope">
            <img v-if="scope.row.picture" :src="baseUrl + scope.row.picture" class="table-img">
          </template>
        </el-table-column>
        <el-table-column prop="buynumber" label="数量" width="80"></el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">￥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="total" label="小计" width="100">
          <template #default="scope">￥{{ scope.row.total }}</template>
        </el-table-column>
        <el-table-column prop="tablename" label="类型" width="120"></el-table-column>
        <el-table-column prop="addtime" label="添加时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const baseUrl = ref('/api/order/file/')
const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  goodname: '',
  userid: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc'
    }
    
    if (searchForm.goodname) {
      params.goodname = searchForm.goodname
    }
    if (searchForm.userid) {
      params.userid = searchForm.userid
    }
    
    const res = await request.get('/order/cart/list', { params })
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
  searchForm.goodname = ''
  searchForm.userid = ''
  handleSearch()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗?', '提示', { type: 'warning' })
    const res = await request.post('/order/cart/delete', [id])
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
    const res = await request.post('/order/cart/delete', ids)
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

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
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
</style>
