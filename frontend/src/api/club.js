import request from '../utils/request'

// 社团
export const getClubs = (params) => request.get('/club', { params })
export const getClub = (id) => request.get(`/club/${id}`)
export const createClub = (data) => request.post('/club', data)
export const updateClub = (data) => request.put('/club', data)
export const approveClub = (id, pass, reason) => request.put(`/club/${id}/approve`, null, { params: { pass, reason } })
export const disbandClub = (id) => request.put(`/club/${id}/disband`)
export const myClubs = () => request.get('/club/mine')
export const joinClub = (id) => request.post(`/club/${id}/join`)
export const leaveClub = (id) => request.delete(`/club/${id}/leave`)
export const getMembers = (id) => request.get(`/club/${id}/members`)
export const getPosts = (id) => request.get(`/club/${id}/posts`)
export const publishPost = (id, data) => request.post(`/club/${id}/post`, data)
