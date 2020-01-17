import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import axios from "axios";
import { Message } from "element-ui";
Vue.use(ElementUI);
// axios.defaults.baseURL = 'http://192.168.50.96:8182'
//http request 拦截器
axios.interceptors.request.use(
  config => {
    config.data = JSON.stringify(config.data);
    config.headers = {
      "Content-Type": "application/json"
    };
    return config;
  },
  error => {
    return Promise.reject(err);
  }
);
// response interceptor
axios.interceptors.response.use(
  response => {
    // console.log('erere', response)
    return response
  },
  error => {
    const rest = error.response;
    if (rest) {
      if (rest.status === 400) {
        // 提示错误
        Message({ message: rest.data.desc, type: "error" });
      }
    } else {
      Message({ message: error, type: "error" });
    }
    console.error('err' + error) // for debug
    return Promise.reject(error)
  }
)
Vue.config.productionTip = false;
Vue.prototype.$http = axios;
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
