/** @jsx jsx */
import { jsx } from "@emotion/core";
import * as KeywordStyle from "./style";
import { Typography, Form, Input, Button, message, Table } from "antd";
import { useRequest } from "ahooks";
import {
  getActionByTime,
  getActionInfo,
} from "../../../services/AnalysisServices";
import { IResponse } from "../../../interfaces";
import {
  IResGetActionByTime,
  ISearchByKeyword,
  IKeywordSearchResData,
} from "../../../interfaces/Analysis";
import { useEffect, useState } from "react";
import { useParams, useLocation, useHistory } from "react-router-dom";
import {
  getTime,
  nullToEmptyString,
  generateUrlWithParams,
  changeNanToDefaultNum,
} from "../../../utils";
import ShareBtn from "../../../components/Analysis/ShareBtn";

const Search = (params: any) => {
  const { Title } = Typography;
  const [form] = Form.useForm();
  const { planID } = useParams();
  const nowTime = getTime();

  const [lastestRunTime, setLastestRunTime] = useState("");
  const [resultDataSource, setResultDataSource] = useState([]);
  const [pageTotal, setPageTotal] = useState(0);
  const [refresh, setRefresh] = useState(true);

  const urlParams = useLocation().search;
  const queryParams = new URLSearchParams(urlParams);
  const history = useHistory();
  const baseUrl = `${params.baseUrl}${planID}`;

  const defaultFormValue = {
    wildcardKeyword: nullToEmptyString(queryParams.get("wildcardKeyword")),
    termKeyword: nullToEmptyString(queryParams.get("termKeyword")),
    termKeywordOr: nullToEmptyString(queryParams.get("termKeywordOr")),
  };

  const columns: any = [
    {
      title: "链接地址",
      dataIndex: "link",
      key: "link",
    },
    {
      title: "html片段",
      dataIndex: "htmlPart",
      key: "htmlPart",
      render: (text: string) => (
        <div dangerouslySetInnerHTML={{ __html: text }}></div>
      ),
    },
    {
      title: "操作",
      fixed: "right",
      width: 230,
      render: (value: any, record: any) => (
        <div>
          <Button type="primary" href={record.link} target="_blank">
            前往该页
          </Button>
          <Button style={{ marginLeft: 15 }}>预览</Button>
        </div>
      ),
    },
  ];

  const getSearchParams = (page?: number, limit?: number): ISearchByKeyword => {
    const formValue = form.getFieldsValue();
    return {
      wildcardKeyword: nullToEmptyString(formValue.wildcardKeyword),
      termKeyword: nullToEmptyString(formValue.termKeyword),
      termKeywordOr: nullToEmptyString(formValue.termKeywordOr),
      actionId: Number(queryParams.get("actionId")),
      planId: Number(queryParams.get("planId")),
      page: page
        ? page
        : changeNanToDefaultNum(Number(queryParams.get("page")), 1),
      limit: limit
        ? limit
        : changeNanToDefaultNum(Number(queryParams.get("limit")), 10),
    };
  };

  const getActionByTimeAction = useRequest(getActionByTime, {
    manual: true,
    onSuccess: (result: IResponse<IResGetActionByTime>, params) => {
      if (result.code === 200) {
        const actionID = result.data.id;
        const searchParams = getSearchParams();
        searchParams.actionId = actionID;
        searchParams.planId = planID;
        history.push(generateUrlWithParams(baseUrl, searchParams));
        setRefresh(!refresh);
      } else {
        message.error(result.msg);
      }
    },
  });

  const getActionInfoAction = useRequest(getActionInfo, {
    manual: true,
    onSuccess: (result, params) => {
      const info = result.data;
      setLastestRunTime(info.lastestRunTime);
      const searchParams = getSearchParams();
      if (
        searchParams.wildcardKeyword ||
        searchParams.termKeyword ||
        searchParams.termKeywordOr
      ) {
        searchByKeywordAction.run(searchParams);
      }
    },
  });

  const searchByKeywordAction = useRequest(params.searchWith, {
    manual: true,
    formatResult: (res: IResponse<IKeywordSearchResData[]>) => {
      const resultData = res.data.map((item: IKeywordSearchResData) => {
        return {
          actionId: item.commonBody.actionId,
          htmlPart: item.highlight,
          link: item.commonBody.url,
          key: item._id,
        };
      });
      return {
        count: res.count,
        data: resultData,
      };
    },
    onSuccess: (result, params) => {
      setResultDataSource(result.data);
      setPageTotal(result.count);
    },
  });

  const onSearch = () => {
    history.push(generateUrlWithParams(baseUrl, getSearchParams(1, 10)));
    setRefresh(!refresh);
  };

  useEffect(() => {
    if (queryParams.get("actionId")) {
      getActionInfoAction.run(Number(queryParams.get("actionId")));
    } else {
      getActionByTimeAction.run({
        time: encodeURI(nowTime),
        planId: planID,
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [refresh]);

  return (
    <KeywordStyle.GStyle>
      <Title level={2} css={KeywordStyle.TitleStyle}>
        {params.searchTitle}
      </Title>
      <Form
        layout="inline"
        form={form}
        css={KeywordStyle.FormStyle}
        initialValues={defaultFormValue}
      >
        <Form.Item>
          <span>数据抓取时间：{lastestRunTime}</span>
        </Form.Item>
        <Form.Item name="termKeyword">
          <Input placeholder="精确关键词（匹配所有）" />
        </Form.Item>
        <Form.Item name="termKeywordOr">
          <Input placeholder="精确关键词（匹配任意一个）" />
        </Form.Item>
        <Form.Item name="wildcardKeyword">
          <Input placeholder="模糊关键词" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" onClick={onSearch} style={{ marginRight: 10 }}>
            查询
          </Button>
          <ShareBtn
            size={"middle"}
            inputText={window.location.href}
            text={"分享"}
            type={"default"}
          />
        </Form.Item>
      </Form>
      <Table
        style={{ marginTop: 30 }}
        dataSource={resultDataSource}
        // scroll={{ x: "max-content" }}
        columns={columns}
        loading={searchByKeywordAction.loading}
        pagination={{
          total: pageTotal,
          defaultCurrent: changeNanToDefaultNum(
            Number(queryParams.get("page")),
            1
          ),
          defaultPageSize: changeNanToDefaultNum(
            Number(queryParams.get("limit")),
            10
          ),
          showSizeChanger: true,
          onChange: (current: number, pageSize?: number) => {
            history.push(
              generateUrlWithParams(baseUrl, getSearchParams(current, pageSize))
            );
            setRefresh(!refresh);
          },
        }}
      />
    </KeywordStyle.GStyle>
  );
};

export default Search;
