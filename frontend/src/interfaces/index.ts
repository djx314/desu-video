import MenuItem from "antd/lib/menu/MenuItem";

export interface IMenuClickEvent {
  key: string;
  keyPath: any;
  item: MenuItem;
  donEvent: any;
}

export interface IResponse<T> {
  code: number;
  msg: string;
  count: number;
  data: T;
}

export interface IPageData {
  current: number,
  pageSize: number,
  filter?: any,
}
