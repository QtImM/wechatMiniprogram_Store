const Layout = () => import("@/layout/index.vue");

export default {
  path: "/member",
  name: "Member",
  component: Layout,
  redirect: "/member/user",
  meta: {
    icon: "ep/user",
    title: "会员中心",
    rank: 4,
    permissions: ["member:manage"]
  },
  children: [
    {
      path: "/member/user",
      name: "MemberList",
      component: () => import("@/views/member/user/index.vue"),
      meta: {
        title: "会员列表",
        permissions: ["member:manage"]
      }
    }
  ]
} satisfies RouteConfigsTable;
