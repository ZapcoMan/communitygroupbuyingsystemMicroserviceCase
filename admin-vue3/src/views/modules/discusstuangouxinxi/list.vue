<template>
  <div class="list-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="评论内容">
          <el-input v-model="searchForm.content" placeholder="请输入评论内容" clearable></el-input>
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
        <el-table-column prop="nickname" label="评论用户" width="120"></el-table-column>
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="reply" label="回复内容" width="200">
          <template #default="scope">
            {{ scope.row.reply || '暂无回复' }}
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="评论时间" width="180"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button type="text" size="small" @click="handleReply(scope.row)">回复</el-button>
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

    <el-dialog v-model="replyVisible" title="回复评论" width="500px">
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="评论内容">
          <el-input v-model="currentComment.content" type="textarea" :rows="3" disabled></el-input>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="replyForm.reply" type="textarea" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const replyVisible = ref(false)
const currentComment = ref({})
const replyForm = reactive({ id: '', reply: '' })

const searchForm = reactive({ content: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc',
      tablename: 'tuangouxinxi'
    }
    
    if (searchForm.content) params.content = searchForm.content
    
    const res = await request.get('/discusstuangouxinxi/list', { params })
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
  searchForm.content = ''
  handleSearch()
}

const handleReply = (row) => {
  currentComment.value = row
  replyForm.id = row.id
  replyForm.reply = row.reply || ''
  replyVisible.value = true
}

const handleSubmitReply = async () => {
  try {
    const res = await request.post('/discusstuangouxinxi/update', replyForm)
    if (res.code === 0) {
      ElMessage.success('回复成功')
      replyVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗?', '提示', { type: 'warning' })
    const res = await request.post('/discusstuangouxinxi/delete', [id])
    if (res.code === 0) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (error) {
    if (error !== 'cancel') console.error('删除失败:', error)
  }
}

const handleBatchDelete = async () => {
  if (!multipleSelection.value.length) {
    ElMessage.warning('请选择要删除的数据')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条数据吗?`, '提示', { type: 'warning' })
    const ids = multipleSelection.value.map(item => item.id)
    const res = await request.post('/discusstuangouxinxi/delete', ids)
    if (res.code === 0) {
      ElMessage.success('批量删除成功')
      multipleSelection.value = []
      fetchData()
    }
  } catch (error) {
    if (error !== 'cancel') console.error('批量删除失败:', error)
  }
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
}

onMounted(() => fetchData())
</script>

<style scoped>
.list-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.toolbar { margin-bottom: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
