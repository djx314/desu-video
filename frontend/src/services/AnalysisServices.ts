import axiosInstance from "./config";
import { AxiosRequestConfig } from "axios";
import { IPageData } from "../interfaces";
import { ISearchByKeyword } from "../interfaces/Analysis";

interface IGetActionByTime {
  time: string;
  planId: number;
}

export const getActionByTime = (params: IGetActionByTime) => {
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/actionByTime",
    params: params,
  };
  return axiosInstance(reqConfig).then((res) => res.data);
};

export const getPlanUrls = (params: any) => {
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/planUrls",
    params: params,
  };
  return axiosInstance(reqConfig).then((res) => res.data);
};

export const getPlansActions = (params: IPageData) => {
  const newParams = {
    page: params.current,
    limit: params.pageSize,
    planId: params.filter,
  };
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/actions",
    params: newParams,
  };
  return axiosInstance(reqConfig).then((res) => res.data);
};

export const getActionInfo = (actionId: number) => {
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/actionById/" + String(actionId),
  };
  return axiosInstance(reqConfig).then((res) => res.data);
};

export const searchByKeyword = (params: ISearchByKeyword) => {
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/keyword",
    params: params,
  };
  return axiosInstance(reqConfig).then((res) => res.data);
};

export const searchBySourceKeyword = (params: ISearchByKeyword) => {
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/sourceKeyword",
    params: params,
  };
  return axiosInstance(reqConfig).then((res) => res.data);
};

