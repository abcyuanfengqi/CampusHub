import request from '../utils/request'

// 二手闲置
export const getItems = (params) => request.get('/secondhand', { params })
export const getItem = (id) => request.get(`/secondhand/${id}`)
export const publishItem = (data) => request.post('/secondhand', data)
export const reserveItem = (id) => request.post(`/secondhand/${id}/reserve`)
export const sellItem = (id) => request.post(`/secondhand/${id}/sell`)
export const offShelfItem = (id) => request.delete(`/secondhand/${id}`)
export const myItems = (params) => request.get('/secondhand/mine', { params })

// 失物招领
export const getLostFounds = (params) => request.get('/lostfound', { params })
export const getLostFound = (id) => request.get(`/lostfound/${id}`)
export const publishLostFound = (data) => request.post('/lostfound', data)
export const claimLostFound = (id) => request.post(`/lostfound/${id}/claim`)
export const resolveLostFound = (id) => request.post(`/lostfound/${id}/resolve`)
export const myLostFounds = (params) => request.get('/lostfound/mine', { params })

// 通知与公告
export const getNotifications = (params) => request.get('/notify', { params })
export const getUnread = () => request.get('/notify/unread')
export const markRead = (id) => request.put(`/notify/${id}/read`)
export const markAllRead = () => request.put('/notify/read-all')
export const getAnnouncements = (limit) => request.get('/notify/announcement', { params: { limit } })
export const publishAnnouncement = (data) => request.post('/notify/announcement', data)

// 数据看板
export const getDashboard = (days) => request.get('/stat/dashboard', { params: { days } })

// 文件
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
}
