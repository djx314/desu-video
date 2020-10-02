import React, { useEffect } from "react";
import { Form, Input } from "antd";
import { FormInstance } from "antd/lib/form";

interface IPlanForm {
  form: FormInstance
  infoValue?: any
}

const PlanForm = (param: IPlanForm) => {

  useEffect(() => {
    param.form.resetFields()
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [param.infoValue])

  return (
    <>
      <Form
        initialValues={param.infoValue}
        layout="vertical"
        form={param.form}
      >
        <Form.Item
          label="计划名称"
          name="name"
          rules={[{ required: true, message: "请输入计划名称" }]}

        >
          <Input />
        </Form.Item>
        <Form.Item
          label="引导链接"
          name="rootUrl"
          rules={[{ required: true, message: "请输入引导链接" }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="最大请求（分）"
          name="minuteLimit"
          rules={[{ required: true, message: "请输入最大请求" }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="最大并发"
          name="maxOccurs"
          rules={[{ required: true, message: "请输入最大并发" }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label="最大深度"
          name="maxDeep"
          rules={[{ required: true, message: "请输入最大深度" }]}
        >
          <Input />
        </Form.Item>
      </Form>
    </>
  );
};

export default PlanForm;
