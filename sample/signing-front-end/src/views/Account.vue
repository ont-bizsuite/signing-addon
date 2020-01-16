<template>
    <div class="account">
        <div class="select_box">
            <h2>
                Do you have username?
            </h2>
        </div>
        <router-view/>
    </div>
</template>

<script>
export default {

}
</script>

<style lang="less" scoped>
.account {
  width: 100%;
  height: 100%;
  max-width: 1020px;
  margin: 0 auto;
}
</style>
