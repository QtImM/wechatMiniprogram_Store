import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 登录只校验必填；密码真实性由后端验证，避免拒绝合法的长密码或历史密码。 */
const loginRules = reactive<FormRules>({
  password: [
    {
      required: true,
      message: "请输入密码",
      trigger: "blur"
    }
  ]
});

export { loginRules };
