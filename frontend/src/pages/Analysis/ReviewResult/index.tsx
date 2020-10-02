import React, { useEffect, useState } from "react";
import { useLocation, useHistory } from "react-router-dom";
import { getPlanUrls, getActionInfo } from "../../../services/AnalysisServices";
import { useRequest } from "ahooks";
import { IReviewParams, IResGetPlanUrls } from "../../../interfaces/Analysis";
import {
  nullToEmptyString,
  changeSizeFormat,
  getTime,
  generateUrlWithParams,
} from "../../../utils";
import { Table, Button, message } from "antd";
import { IResponse } from "../../../interfaces";
import * as ReviewResultStyle from "./style";
import ShareBtn from "../../../components/Analysis/ShareBtn";

const ReviewResult = () => {
  const columns: any = [
    {
      title: "状态码",
      dataIndex: "status",
      key: "status",
    },
    {
      title: "请求耗时",
      dataIndex: "wasteTime",
      key: "wasteTime",
    },
    {
      title: "目标链接",
      dataIndex: "url",
      key: "url",
    },
    {
      title: "主机头",
      dataIndex: "host",
      key: "host",
    },
    {
      title: "协议",
      dataIndex: "protocol",
      key: "protocol",
    },
    {
      title: "ContentType",
      dataIndex: "contentType",
      key: "contentType",
    },
    {
      title: "大小",
      dataIndex: "contentLength",
      key: "contentLength",
    },
    {
      title: "深度",
      dataIndex: "deep",
      key: "deep",
    },
    {
      title: "请求时间",
      dataIndex: "requestTime",
      key: "requestTime",
    },
  ];
  const urlParams = useLocation().search;
  const queryParams = new URLSearchParams(urlParams);

  const [resultDataSource, setResultDataSource] = useState<IResGetPlanUrls[]>(
    []
  );
  const [pageTotal, setPageTotal] = useState(0);
  const [refresh, setRefresh] = useState(true);
  const [lastestRunTime, setLastestRunTime] = useState("");

  const searchParams: IReviewParams = {
    codeArrayExclude: nullToEmptyString(queryParams.get("codeArrayExclude")),
    codeArrayInclude: nullToEmptyString(queryParams.get("codeArrayInclude")),
    hostExclude: nullToEmptyString(queryParams.get("hostExclude")),
    hostInclude: nullToEmptyString(queryParams.get("hostInclude")),
    protocolExclude: nullToEmptyString(queryParams.get("protocolExclude")),
    protocolInclude: nullToEmptyString(queryParams.get("protocolInclude")),
    urlExclude: nullToEmptyString(queryParams.get("urlExclude")),
    urlInclude: nullToEmptyString(queryParams.get("urlInclude")),
    typeExclude: nullToEmptyString(queryParams.get("typeExclude")),
    typeInclude: nullToEmptyString(queryParams.get("typeInclude")),
    refererExclude: nullToEmptyString(queryParams.get("refererExclude")),
    refererInclude: nullToEmptyString(queryParams.get("refererInclude")),
    minSize: nullToEmptyString(queryParams.get("minSize")),
  };

  const searchParamsText = JSON.stringify(searchParams);

  const getActionInfoAction = useRequest(
    getActionInfo,
    {
      manual: true,
      onSuccess: (result, params) => {
        setLastestRunTime(result.data.lastestRunTime)
      }
    }
  )

  const backToReview = () => {
    if (queryParams.get("planId") && queryParams.get("actionId")) {
      let newSearchParams = searchParams;
      newSearchParams.actionId = nullToEmptyString(queryParams.get("actionId"));
      const baseUrl = "/plan/review/params/" + queryParams.get("planId");
      const url = generateUrlWithParams(baseUrl, newSearchParams);
      history.push(url);
    } else {
      message.error("返回失败，请重试！");
    }
  };

  const history = useHistory();

  const getPlanUrlsAction = useRequest(getPlanUrls, {
    loadingDelay: 1000,
    manual: true,
    formatResult: (res: IResponse<IResGetPlanUrls[]>) => {
      const resultData = res.data.map((item: IResGetPlanUrls) => {
        item.key = item._id;
        item.wasteTime = `${item.wasteTime}ms`;
        item.contentLength = changeSizeFormat(item.contentLength);
        item.requestTime = getTime(item.requestTime);
        return item;
      });
      res.data = resultData;
      return res;
    },
    onSuccess: (result: IResponse<IResGetPlanUrls[]>, params) => {
      setResultDataSource(result.data);
      setPageTotal(result.count);
    },
  });

  useEffect(() => {
    const params: IReviewParams = searchParams;
    params.actionId = nullToEmptyString(queryParams.get("actionId"));
    params.planId = nullToEmptyString(queryParams.get("planId"));
    params.page = nullToEmptyString(queryParams.get("page"));
    params.limit = nullToEmptyString(queryParams.get("limit"));
    getPlanUrlsAction.run(params);
    getActionInfoAction.run(Number(params.actionId))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [refresh]);

  return (
    <>
      <ReviewResultStyle.FirstLineStyle>
      <span>数据抓取时间：{lastestRunTime}</span>
        <ShareBtn
          size={"middle"}
          inputText={window.location.href}
          text={"分享"}
        />
        <ShareBtn
          size={"middle"}
          inputText={searchParamsText}
          text={"只分享参数"}
        />
        <Button type="primary" onClick={backToReview}>
          返回参数设置
        </Button>
        <Button
          type="primary"
          href={`http://${
            window.location.hostname
          }:486/back/excel/export/${queryParams.get("planId")}${urlParams}`}
          target="_blank"
        >
          导出（仅链接）
        </Button>
        <Button
          type="primary"
          href={`http://${
            window.location.hostname
          }:486/back/excel/export/referer/${queryParams.get(
            "planId"
          )}${urlParams}`}
          target="_blank"
        >
          导出（包括referer）
        </Button>
      </ReviewResultStyle.FirstLineStyle>
      <Table
        dataSource={resultDataSource}
        columns={columns}
        scroll={{ x: "max-content" }}
        pagination={{
          total: pageTotal,
          defaultCurrent: Number(queryParams.get("page")),
          defaultPageSize: Number(queryParams.get("limit")),
          showSizeChanger: true,
          onChange: (current: number, pageSize?: number) => {
            queryParams.set("limit", String(pageSize));
            queryParams.set("page", String(current));
            const url = "/plan/review?" + queryParams.toString();
            history.push(url);
            setRefresh(!refresh);
          },
        }}
      />
      ;
    </>
  );
};

export default ReviewResult;
