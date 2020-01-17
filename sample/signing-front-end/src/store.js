import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {
    async sendONS({ dispatch, commit }, params) {
      try {
        return axios.post(process.env.VUE_APP_API + '/api/v1/app/register', params)
      } catch (error) {
        return error
      }
    },
    async checkSignUp({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_API + '/api/v1/app/register/result/' + params
        )
      } catch (error) {
        return error
      }
    },
    async getLoginMsg({ dispatch, commit }, params) {
      try {
        return axios.post(process.env.VUE_APP_API + '/api/v1/app/login')
      } catch (error) {
        return error
      }
    },
    async getLoginRes({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_API + '/api/v1/app/login/result/' + params
        )
      } catch (error) {
        return error
      }
    },
    async getInvokeMsg({ dispatch, commit }, params) {
      try {
        return axios.post(process.env.VUE_APP_API + '/api/v1/app/invoke/' + params)
      } catch (error) {
        return error
      }
    },
    async checkInvoke({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_API + '/api/v1/app/invoke/result/' + params
        )
      } catch (error) {
        return error
      }
    }
  }
})
