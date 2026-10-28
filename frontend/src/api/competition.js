import request from '../utils/request'

// 竞赛组队
export const getCompetitions = (params) => request.get('/competition', { params })
export const getCompetition = (id) => request.get(`/competition/${id}`)
export const createCompetition = (data) => request.post('/competition', data)
export const getTeams = (id) => request.get(`/competition/${id}/teams`)
export const createTeam = (id, data) => request.post(`/competition/${id}/team`, data)
export const joinTeam = (teamId) => request.post(`/competition/team/${teamId}/join`)
export const leaveTeam = (teamId) => request.delete(`/competition/team/${teamId}/leave`)
export const getTeamMembers = (teamId) => request.get(`/competition/team/${teamId}/members`)
export const myTeams = () => request.get('/competition/mine')
