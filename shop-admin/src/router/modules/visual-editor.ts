const Layout = () => import("@/layout/index.vue");

export default {
    path: "/visual-editor",
    name: "VisualEditor",
    component: Layout,
    redirect: "/visual-editor/home",
    meta: {
        icon: "ep/monitor",
        title: "可视化装修",
        rank: 0
    },
    children: [
        {
            path: "/visual-editor/home",
            name: "VisualEditorHome",
            component: () => import("@/views/content/preview-center/index.vue"),
            meta: {
                title: "实时预览与编辑",
                permissions: ["content:manage", "content:read", "product:manage", "product:read"]
            }
        }
    ]
} satisfies RouteConfigsTable;
