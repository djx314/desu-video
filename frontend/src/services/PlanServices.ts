import axiosInstance from './config';
import { AxiosRequestConfig } from "axios";
import { IReqData } from "../interfaces/Plan/index"
import { IPageData } from "../interfaces"


export const getPlans = (params: IPageData) => {
  const newParams = {
    page: params.current,
    limit: params.pageSize
  }
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/plans",
    params: newParams,
  }
  return axiosInstance(reqConfig).then(res => res.data)
}


export const addPlan = <T>(reqData: Promise<IReqData | null>): Promise<T> => {
  return reqData.then(reqData => {
    const reqConfig: AxiosRequestConfig = {
      method: "post",
      url: "/plan",
      data: reqData,
    }
    return axiosInstance(reqConfig).then(res => res.data)
  })
}


export const delPlan = (planID: number) => {
  const reqConfig: AxiosRequestConfig = {
    method: "delete",
    url: "/plan/" + planID,
  }
  return axiosInstance(reqConfig).then(res => res.data)
}


export const getPlanInfo = (planID: number) => {
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/plan/" + planID,
  }
  return axiosInstance(reqConfig).then(res => res.data)
}


export const modifyPlan = <T>(reqData: Promise<IReqData | null>): Promise<T> => {
  return reqData.then(reqData => {
    const reqConfig: AxiosRequestConfig = {
      method: "put",
      url: "/plan",
      data: reqData,
    }
    return axiosInstance(reqConfig).then(res => res.data)
  })
}

export const startPlanTask = (planID: number) => {
  const reqConfig: AxiosRequestConfig = {
    method: "get",
    url: "/test02/" + planID,
  }
  return axiosInstance(reqConfig).then(res => res.data)
}
