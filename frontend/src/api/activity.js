import request from '../utils/request'

// 活动
export const getActivities = (params) => request.get('/activity', { params })
export const getActivity = (id) => request.get(`/activity/${id}`)
export const createActivity = (data) => request.post('/activity', data)
export const updateActivity = (data) => request.put('/activity', data)
export const publishActivity = (id) => request.put(`/activity/${id}/publish`)
export const openActivity = (id) => request.put(`/activity/${id}/open`)
export const finishActivity = (id) => request.put(`/activity/${id}/finish`)
export const cancelActivity = (id) => request.put(`/activity/${id}/cancel`)
export const registerActivity = (id) => request.post(`/activity/${id}/register`)
export const cancelRegister = (id) => request.delete(`/activity/${id}/register`)
export const checkinActivity = (id, code) => request.post(`/activity/${id}/checkin`, null, { params: { code } })
export const myRegistrations = () => request.get('/activity/registrations/mine')
export const myActivities = () => request.get('/activity/mine')
