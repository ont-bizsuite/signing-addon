<template>
  <div class="login">
    <div class="title">Sign In</div>
    <div class="domain">Domain: {{ domain }}</div>
    <div class="tips">
      No account yet?,
      <span @click="$router.push({ path: '/register' })">SIGN UP</span>
    </div>
    <div class="qrcode">
      <img :src="url" alt />
    </div>
  </div>
</template>

<script>
import QRCode from 'qrcode'
export default {
  data() {
    return {
      url: '',
      dataId: '',
      getResTimer: null,
      domain: 'on.ont'
    }
  },
  methods: {
    createQRcode(info) {
      console.log('codeParams', info)
      let codeParams = JSON.stringify(info)
      QRCode.toDataURL(codeParams)
        .then(url => {
          this.url = url
        })
        .catch(err => {})

      this.getResTimer = setInterval(() => {
        this.getLoginResult()
      }, 3000)
    },
    async getLoginResult() {
      try {
        let res = await this.$store.dispatch('getLoginRes', this.dataId)
        console.log('getLoginResult', res)
        if (res.data.desc === 'SUCCESS') {
          if (res.data.result && res.data.result.result === '1') {
            sessionStorage.setItem('ons', res.data.result.userName)
            sessionStorage.setItem('ontid', res.data.result.ontid)
            this.$message({
              message: 'Sign In Successful',
              center: true,
              type: 'success'
            })
            clearInterval(this.getResTimer)
            this.$router.push({ path: '/' })
            return false
          } else if (res.data.result && res.data.result.result === '2') {
            this.$message({
              message: 'Please Sign Up ONS',
              center: true,
              type: 'error'
            })
            clearInterval(this.getResTimer)
            this.$router.push({ path: '/register' })
            return false
          }
        } else {
          this.$message({
            message: 'Get Sign In Result Fail!',
            center: true,
            type: 'error'
          })
          clearInterval(this.getResTimer)
          return false
        }
      } catch (error) {
        clearInterval(this.getResTimer)
        return false
      }
    }
  },
  async mounted() {
    try {
      let result = await this.$store.dispatch('getLoginMsg')
      console.log('loginmsg', result)
      if (result.data.desc === 'SUCCESS') {
        let info = result.data.result.qrCode
        console.log('info', info)
        this.dataId = result.data.result.id
        this.createQRcode(info)
      } else {
        this.$message({
          message: 'Get Message Fail!',
          center: true,
          type: 'error'
        })
        return false
      }
    } catch (error) {
      return false
    }
  },
  beforeDestroy() {
    clearInterval(this.getResTimer)
  }
}
</script>
<style lang="less" scoped>
.login {
  width: 100%;
  height: 100%;
  max-width: 1020px;
  margin: 0 auto;
  .domain {
    font-size: 20px;
    line-height: 36px;
  }
  .title {
    font-size: 50px;
    font-weight: bold;
    line-height: 200px;
    padding-top: 100px;
  }
  .tips {
    font-size: 20px;
    span {
      color: #2472cc;
      text-decoration: underline;
      cursor: pointer;
    }
  }
  .qrcode {
    width: 200px;
    height: 200px;
    margin: 0 auto;
    margin-top: 50px;
    // background: tomato;
    img {
      width: 100%;
      height: 100%;
      display: block;
    }
  }
}
</style>
