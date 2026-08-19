import { http } from "@/utils/http";
import type { PageResult, MemberUser } from "./types";

// ==================== 会员 ====================
export const getMemberPage = (params: {
  pageNo?: number;
  pageSize?: number;
  nickname?: string;
  mobile?: string;
}) => {
  return http.get<PageResult<MemberUser>, typeof params>(
    "/admin-api/member/user/page",
    { params }
  );
};

export const getMemberDetail = (id: number) => {
  return http.get<MemberUser, { id: number }>(
    "/admin-api/member/user/detail",
    { params: { id } }
  );
};
