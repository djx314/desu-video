/** @jsx jsx */
import { jsx } from "@emotion/core";
import { FC, useState, useEffect } from "react";
import {
  Form,
  Input,
  Typography,
  DatePicker,
  Row,
  Col,
  Button,
  message,
  Modal,
} from "antd";
import moment from "moment";
import * as ReviewStyle from "./style";
import { useParams, useHistory, useLocation } from "react-router-dom";
import { getActionByTime } from "../../../services/AnalysisServices";
import { useRequest } from "ahooks";
import { IReviewParams } from "../../../interfaces/Analysis";
import { generateUrlWithParams } from "../../../utils";
import { IResponse } from "../../../interfaces";
import { IResGetActionByTime } from "../../../interfaces/Analysis";
import ShareBtn from "../../../components/Analysis/ShareBtn";
import { nullToEmptyString } from "../../../utils";

const Review: FC = () => {
  const leftWidth = 5;
  const rightWidth = 19;
  let history = useHistory();
  const queryParams = new URLSearchParams(useLocation().search);
  const [defaultFormValue, setDefaultFormValue] = useState<IReviewParams>({
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
    actionId: nullToEmptyString(queryParams.get("actionId")),
  });

  const { planID } = useParams();

  const [form] = Form.useForm();

  const { Title, Paragraph } = Typography;

  const formItemLayout = {
    labelCol: { span: leftWidth },
    wrapperCol: { span: rightWidth },
  };

  const toReviewPage = (actionID: number) => {
    const formValue = form.getFieldsValue();
    const params: IReviewParams = {
      actionId: actionID,
      planId: planID,
      page: "1",
      limit: "10",
      codeArrayExclude: formValue.codeArrayExclude,
      codeArrayInclude: formValue.codeArrayInclude,
      hostExclude: formValue.hostExclude,
      hostInclude: formValue.hostInclude,
      protocolExclude: formValue.protocolExclude,
      protocolInclude: formValue.protocolInclude,
      urlExclude: formValue.urlExclude,
      urlInclude: formValue.urlInclude,
      typeExclude: formValue.typeExclude,
      typeInclude: formValue.typeInclude,
      refererExclude: formValue.refererExclude,
      refererInclude: formValue.refererInclude,
      minSize: formValue.minSize,
    };
    const url = generateUrlWithParams("/plan/review", params);
    history.push(url);
  };

  const getActionByTimeAction = useRequest(getActionByTime, {
    manual: true,
    onSuccess: (result: IResponse<IResGetActionByTime>, params) => {
      if (result.code === 200) {
        const actionID = result.data.id;
        toReviewPage(actionID);
      } else {
        message.error(result.msg);
      }
    },
  });

  const [searchParamsText, setSearchParamsText] = useState(
    JSON.stringify(defaultFormValue)
  );
  const [showPasteParamsModal, setShowPasteParamsModal] = useState(false);
  const [pasteParamsText, setPasteParamsText] = useState("");

  const searchResult = () => {
    if (queryParams.get("actionId")) {
      toReviewPage(
        Number(queryParams.get("actionId"))
      );
    } else {
      const formValue = form.getFieldsValue();
      getActionByTimeAction.run({
        time: encodeURI(formValue.time.format("YYYY-MM-DD HH:mm:ss")),
        planId: planID,
      });
    }
  };

  const pasteParams = () => {
    const pasteParamsJson: IReviewParams = JSON.parse(pasteParamsText);
    setShowPasteParamsModal(false);
    const baseUrl = "/plan/review/params/" + planID;
    const url = generateUrlWithParams(baseUrl, pasteParamsJson);
    history.push(url);
    setDefaultFormValue(pasteParamsJson)
  };

  const showEndTime = () => {
    if (defaultFormValue.actionId) {
      return (
        <Form.Item
          label="截止时间"
          name="time"
          initialValue="已绑定具体抓取任务"
        >
          <p style={{ marginBottom: 0 }}>已绑定具体抓取任务</p>
        </Form.Item>
      );
    } else {
      return (
        <div>
          <Form.Item
            label="截止时间"
            name="time"
            initialValue={moment(new Date(), "YYYY-MM-DD HH:mm:ss")}
          >
            <DatePicker
              showTime
              format="YYYY-MM-DD HH:mm:ss"
              style={{ width: "100%" }}
            />
          </Form.Item>
          <Row>
            <Col span={leftWidth}></Col>
            <Col span={rightWidth}>
              <Paragraph>
                需要搜索的内容的截止时间，后台将会自动匹配在这个时间之前的并且最接近这个时间的抓取计划进行搜索。
              </Paragraph>
            </Col>
          </Row>
        </div>
      );
    }
  };

  useEffect(() => {
    form.setFieldsValue(defaultFormValue)
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [defaultFormValue])

  return (
    <ReviewStyle.GStyle>
      <Title level={2} css={ReviewStyle.TitleStyle}>
        网站资源监控参数设置
      </Title>
      <Form
        {...formItemLayout}
        layout="horizontal"
        form={form}
        css={ReviewStyle.FormStyle}
        initialValues={defaultFormValue}
        onValuesChange={(changedValues, allValues) => {
          const searchParams: any = allValues;
          if (defaultFormValue.actionId) {
            searchParams.actionId = defaultFormValue.actionId;
          }
          if (searchParams.time === "已绑定具体抓取任务") {
            searchParams.time = "";
          }
          setSearchParamsText(JSON.stringify(searchParams));
        }}
      >
        {showEndTime()}
        <Form.Item label="状态码不包含" name="codeArrayExclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该排除的状态码，一般而言，200 代表成功，404
              代表找不到（死链），503 代表服务器拒绝返回， 其中 1001
              是自定义状态码，代表请求发生异常，大部分原因是该链接不是有效的网址或者请求发生异常（如超时，目标链接不可用等）。
            </Paragraph>
            <Paragraph>
              此项如果输入 200,201
              （多个状态码使用英文逗号分隔）则代表查询状态码既不是 200 也不是
              201 的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="状态码包含" name="codeArrayInclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该包含的状态码，状态码含义与“状态码不包含”项一样。此项如果输入
              404，1001 （多个状态码使用英文逗号分隔）则代表查询状态码可能是 404
              或 1001 的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="主机头不包含" name="hostExclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该排除的主机头，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入 *.heshan.gov.cn,www.baidu.com
              （多个状态码使用英文逗号分隔） 则代表查询主机头既不以
              heshan.gov.cn 结尾也不是 heshan.gov.cn 的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="主机头包含" name="hostInclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该包含的主机头，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入 *.heshan.gov.cn,www.baidu.com
              （多个状态码使用英文逗号分隔） 则代表查询主机头既有可能以
              heshan.gov.cn 结尾也有可能是 heshan.gov.cn 的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="协议不包含" name="protocolExclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该排除的协议，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入 http*,mailto
              （多个状态码使用英文逗号分隔） 则代表查询主机头既不以 http
              开头也不是 mailto 的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="协议包含" name="protocolInclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该包含的协议，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入 http*,mailto
              （多个状态码使用英文逗号分隔） 则代表查询主机头既有可能以 http
              开头也有可能是 mailto 的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="链接地址不包含" name="urlExclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该排除的链接地址，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入
              http://www.baidu.com/,http://www.heshan.gov.cn/*
              （多个状态码使用英文逗号分隔） 则代表查询主机头既不是
              http://www.baidu.com/ 也不以 http://www.heshan.gov.cn/ 开头的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="链接地址包含" name="urlInclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该包含的链接地址，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入
              http://www.baidu.com/，http://www.heshan.gov.cn/*
              （多个状态码使用英文逗号分隔） 则代表查询主机头既有可能是
              http://www.baidu.com/ 也有可能以 http://www.heshan.gov.cn/
              开头的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="Content-Type不包含" name="typeExclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该排除的 Content-Type，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。
            </Paragraph>
            <Paragraph>
              此项如果输入 text/html,image/* （多个状态码使用英文逗号分隔）
              则代表查询 Content-Type 既不是 text/html 也不以 image/
              开头的项。一般 image/* 代表匹配所有图片。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="Content-Type包含" name="typeInclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该包含的链接地址，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入 text/html,image/*
              （多个状态码使用英文逗号分隔） 则代表查询主机头既有可能是
              text/html 也有可能以 image/ 开头的项。一般 image/*
              代表匹配所有图片。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="referer不包含" name="refererExclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该排除的 referer，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入 lolita,shenshi/*
              （多个状态码使用英文逗号分隔） 则代表查询 referer 既不是 lolita
              也不以 shenshi/ 开头的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="referer包含" name="refererInclude">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              应该包含的 referer，可以使用 * 匹配 0 到多个字符和使用 ?
              匹配单个字符。此项如果输入 lolita,shenshi/*
              （多个状态码使用英文逗号分隔） 则代表查询主机头既有可能是 lolita
              也有可能以 shenshi/ 开头的项。
            </Paragraph>
          </Col>
        </Row>
        <Form.Item label="最小文件大小（KB）" name="minSize">
          <Input />
        </Form.Item>
        <Row>
          <Col span={leftWidth}></Col>
          <Col span={rightWidth}>
            <Paragraph>
              最小的文件大小，输入 -1 或者不输入代表不作限制，单位为 KB。
            </Paragraph>
          </Col>
        </Row>
      </Form>

      <ReviewStyle.LastBtnStyle>
        <Row>
          <Col span={leftWidth}></Col>
          <Button type="primary" size="large" onClick={searchResult}>
            查询
          </Button>
          <ShareBtn
            size={"large"}
            inputText={window.location.href}
            text={"分享"}
          />
          <ShareBtn
            size={"large"}
            inputText={searchParamsText}
            text={"只分享参数"}
          />
          <Button
            type="primary"
            size="large"
            onClick={() => {
              setShowPasteParamsModal(true);
            }}
          >
            粘贴参数
          </Button>
          <Modal
            title="请粘贴参数"
            visible={showPasteParamsModal}
            okText="复制"
            cancelText="取消"
            onCancel={() => {
              setShowPasteParamsModal(false);
            }}
            destroyOnClose
            footer={[
              <Button
                key="copy"
                type="primary"
                onClick={pasteParams}
                className="copy-btn"
              >
                提交
              </Button>,
            ]}
          >
            <Input
              autoFocus={true}
              onChange={(e) => {
                setPasteParamsText(e.target.value);
              }}
            />
          </Modal>
          <Button type="primary" size="large">
            图片资源监控
          </Button>
        </Row>
      </ReviewStyle.LastBtnStyle>
    </ReviewStyle.GStyle>
  );
};

export default Review;
