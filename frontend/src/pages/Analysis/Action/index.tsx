/** @jsx jsx */
import { jsx } from "@emotion/core";
import * as ActionStyle from "./style";
import { Typography, Table, Button } from "antd";
import { getPlansActions } from "../../../services/AnalysisServices";
import { useRequest } from "ahooks";
import { IPlanActionsResData } from "../../../interfaces/Analysis";
import { IResponse } from "../../../interfaces";
import { ColumnProps } from "antd/es/table";
import { useParams, useHistory } from "react-router-dom";
import { useEffect, useState } from "react";
import { booleanToString, generateUrlWithParams } from "../../../utils";

const Action = () => {
  const history = useHistory();

  const columns: ColumnProps<IPlanActionsResData>[] = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "等待中的 url 数量",
      dataIndex: "urlsLeft",
      key: "urlsLeft",
    },
    {
      title: "是否已完成",
      dataIndex: "isFinishText",
      key: "isFinishText",
    },
    {
      title: "是否运行中",
      dataIndex: "isRunningText",
      key: "isRunningText",
    },
    {
      title: "最近启动时间",
      dataIndex: "lastestRunTime",
      key: "lastestRunTime",
    },
    {
      title: "完成时间",
      dataIndex: "finishedTime",
      key: "finishedTime",
    },
    {
      title: "操作",
      dataIndex: "right",
      width: 370,
      render: (value: any, record: IPlanActionsResData) => (
        <ActionStyle.BtnStyle>
          <Button
            size="small"
            type="primary"
            onClick={() => {
              const params = {
                actionId: record.id,
              };
              const baseUrl = `/plan/review/params/${record.planId}`;
              const url = generateUrlWithParams(baseUrl, params);
              history.push(url);
            }}
          >
            资源监控
          </Button>
          <Button
            size="small"
            onClick={() => {
              const params = {
                actionId: record.id,
                planId: record.planId,
                page: 1,
                limit: 10,
              };
              const baseUrl = `/plan/keyword/${record.planId}`;
              const url = generateUrlWithParams(baseUrl, params);
              history.push(url);
            }}
          >
            关键词搜索
          </Button>
          <Button
            size="small"
            onClick={() => {
              const params = {
                actionId: record.id,
                planId: record.planId,
                page: 1,
                limit: 10,
              };
              const baseUrl = `/plan/sourceKeyword/${record.planId}`;
              const url = generateUrlWithParams(baseUrl, params);
              history.push(url);
            }}
          >
            源码搜索
          </Button>
          <Button size="small" onClick={() => {}}>
            溯源
          </Button>
        </ActionStyle.BtnStyle>
      ),
    },
  ];

  const { Title } = Typography;
  const { planID } = useParams();
  const [resultDataSource, setResultDataSource] = useState<
    IPlanActionsResData[]
  >([]);
  const [pageTotal, setPageTotal] = useState(0);

  const getPlansActionsAction = useRequest(getPlansActions, {
    manual: true,
    formatResult: (res: IResponse<IPlanActionsResData[]>) => {
      const resultData = res.data.map((item: IPlanActionsResData) => {
        item.key = item.id;
        item.isFinishText = booleanToString(item.isFinish);
        item.isRunningText = booleanToString(item.isRunning);
        item.lastestRunTime = item.lastestRunTime
          ? item.lastestRunTime.substring(0, 19)
          : "";
        item.finishedTime = item.finishedTime
          ? item.finishedTime.substring(0, 19)
          : "";
        return item;
      });
      res.data = resultData;
      return res;
    },
    onSuccess: (result: IResponse<IPlanActionsResData[]>, params) => {
      setResultDataSource(result.data);
      setPageTotal(result.count);
    },
  });

  useEffect(() => {
    getPlansActionsAction.run({
      current: 1,
      pageSize: 10,
      filter: planID,
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [planID]);

  return (
    <ActionStyle.GStyle>
      <Title level={2} css={ActionStyle.TitleStyle}>
        抓取任务浏览
      </Title>
      <Table
        dataSource={resultDataSource}
        columns={columns}
        pagination={{
          total: pageTotal,
        }}
      />
    </ActionStyle.GStyle>
  );
};

export default Action;
