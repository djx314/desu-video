import React, { FC, useEffect, useState } from "react";
import { Table, Button, message, Popconfirm, Dropdown } from "antd";
import { ColumnProps } from "antd/es/table";
import AddPlan from "../../components/Plan/AddPlan";
import ModifyPlan from "../../components/Plan/ModifyPlan";
import AnalysisLink from "../../components/Plan/AnalysisLink";
import * as PlanStyle from "./style";
import { getPlans, delPlan, startPlanTask } from "../../services/PlanServices";
import { useRequest } from "ahooks";
import {
  QuestionCircleOutlined,
  DownOutlined,
  PlayCircleOutlined,
} from "@ant-design/icons";
import { IResData } from "../../interfaces/Plan/index";
import { IResponse } from "../../interfaces";

const Plan: FC = () => {
  const columns: ColumnProps<IResData>[] = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "计划名称",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "引导链接",
      dataIndex: "rootUrl",
      key: "rootUrl",
    },
    {
      title: "最大请求（分）",
      dataIndex: "minuteLimit",
      key: "minuteLimit",
    },
    {
      title: "最大并发",
      dataIndex: "maxOccurs",
      key: "maxOccurs",
    },
    {
      title: "最大深度",
      dataIndex: "maxDeep",
      key: "maxDeep",
    },
    {
      title: "最近启动时间",
      dataIndex: "lastestRunTime",
      key: "lastestRunTime",
    },
    {
      title: "操作",
      fixed: "right",
      width: 270,
      render: (value: any, record: IResData) => (
        <>
          <Popconfirm
            placement="topRight"
            title={`确定启动ID:${record.id}的抓取任务？`}
            onConfirm={() => startPlan(record.id)}
            okText="确定"
            cancelText="取消"
            icon={<PlayCircleOutlined />}
          >
            <Button type="primary" size="small" className="action_button">
              启动
            </Button>
          </Popconfirm>
          <Dropdown overlay={AnalysisLink({ select: record.id })}>
            <Button type="default" size="small" className="action_button">
              分析 <DownOutlined />
            </Button>
          </Dropdown>
          <Button
            size="small"
            danger
            className="action_button"
            onClick={() => {
              modifyPlan(record.id);
            }}
          >
            修改
          </Button>

          <Popconfirm
            placement="topRight"
            title={`确定删除ID:${record.id}的抓取任务？`}
            onConfirm={() => deletePlan(record.id)}
            okText="确定"
            cancelText="取消"
            icon={<QuestionCircleOutlined style={{ color: "red" }} />}
          >
            <Button
              type="primary"
              size="small"
              danger
              className="action_button"
            >
              删除
            </Button>
          </Popconfirm>
        </>
      ),
    },
  ];
  const modifyPlan = (id: number) => {
    setModifyPlanID(id);
    setModifyPlanVisible(true);
  };
  const startPlan = (id: number) => {
    startPlanAction.run(id);
  };
  const deletePlan = (id: number) => {
    deletePlanAction.run(id);
  };
  const deletePlanAction = useRequest(delPlan, {
    manual: true,
    onSuccess: () => {
      message.success("删除成功");
      const pageConfig = plansDataAction.pagination;
      const params = {
        current: pageConfig.current,
        pageSize: pageConfig.pageSize,
      };
      if (
        pageConfig.total % pageConfig.pageSize === 1 &&
        pageConfig.current > 1
      ) {
        params.current = pageConfig.current - 1;
      }
      plansDataAction.run(params);
    },
    onError: () => message.error("删除失败"),
  });

  const handleAddPlanModalVisible = () => {
    setAddPlanVisible(true);
  };
  const handleAddPlanModalVisibleCancle = () => {
    setAddPlanVisible(false);
  };
  const handleAddPlanModalLoading = () => {
    setAddPlanLoading(true);
  };
  const handleAddPlanModalLoadingCancle = () => {
    setAddPlanLoading(false);
  };
  const [addPlanLoading, setAddPlanLoading] = useState(false);
  const [addPlanVisible, setAddPlanVisible] = useState(false);

  const handleModifyPlanModalVisible = () => {
    setModifyPlanVisible(true);
  };
  const handleModifyPlanModalVisibleCancle = () => {
    setModifyPlanVisible(false);
  };
  const handleModifyPlanModalLoading = () => {
    setModifyPlanLoading(true);
  };
  const handleModifyPlanModalLoadingCancle = () => {
    setModifyPlanLoading(false);
  };
  const [modifyPlanLoading, setModifyPlanLoading] = useState(false);
  const [modifyPlanVisible, setModifyPlanVisible] = useState(false);
  const [modifyPlanID, setModifyPlanID] = useState(0);

  const plansDataAction = useRequest((params) => getPlans({ ...params }), {
    paginated: true,
    loadingDelay: 1000,
    formatResult: (res: IResponse<IResData[]>) => {
      const resultData = res.data.map((item) => {
        item.key = item.id;
        return item;
      });
      return {
        list: resultData,
        total: resultData ? resultData.length : 0,
      };
    },
  });

  const startPlanAction = useRequest(startPlanTask, {
    manual: true,
    onSuccess: (result, params) => {
      message.success(result.msg);
    },
  });

  useEffect(() => {
    document.title = "抓取计划管理";
  });

  return (
    <PlanStyle.GStyle>
      <Button type="primary" onClick={plansDataAction.refresh}>
        刷新
      </Button>
      <Button
        style={{ marginLeft: "10px" }}
        type="primary"
        ghost
        onClick={handleAddPlanModalVisible}
      >
        新增抓取计划
      </Button>
      <div style={{ height: "10px" }} />
      <Table {...plansDataAction.tableProps} columns={columns} />
      <AddPlan
        loading={addPlanLoading}
        visible={addPlanVisible}
        handleModalVisible={handleAddPlanModalVisible}
        handleModalVisibleCancle={handleAddPlanModalVisibleCancle}
        handleModalLoading={handleAddPlanModalLoading}
        handleModalLoadingCancle={handleAddPlanModalLoadingCancle}
        refreshData={plansDataAction.refresh}
      />
      <ModifyPlan
        loading={modifyPlanLoading}
        visible={modifyPlanVisible}
        handleModalVisible={handleModifyPlanModalVisible}
        handleModalVisibleCancle={handleModifyPlanModalVisibleCancle}
        handleModalLoading={handleModifyPlanModalLoading}
        handleModalLoadingCancle={handleModifyPlanModalLoadingCancle}
        refreshData={plansDataAction.refresh}
        modifyPlanID={modifyPlanID}
      />
    </PlanStyle.GStyle>
  );
};

export default Plan;
