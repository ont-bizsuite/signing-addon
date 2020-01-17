import Vue from "vue";
import Router from "vue-router";
import Home from "./views/Home.vue";
import Login from "./views/Login.vue";
import Regiter from "./views/Regiter.vue";
Vue.use(Router);

const router = new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "home",
      component: Home
    },
    {
      path: "/login",
      name: "login",
      component: Login
    },
    {
      path: "/register",
      name: "register",
      component: Regiter
    },
    {
      path: "*",
      redirect: "/"
    }
  ]
});

function jumpTo(target, origin, next) {
  if (target.name !== origin.name) {
    return next(target);
  }
  return next();
}

router.beforeEach(async (to, from, next) => {

  let toPath = to.name
  // console.log(toPath)
  if (toPath === 'login' || toPath === 'register') {
    next()
    return
  }
  let ons = sessionStorage.getItem("ons");
  // if no user_ontid then go web_home
  if (!ons) {
    sessionStorage.clear()
    jumpTo({ name: 'login' }, to, next)
    return
  }
  next()
})

export default router
