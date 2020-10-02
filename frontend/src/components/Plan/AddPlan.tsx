import React from "react";
import { Modal, Button, Form, message } from "antd";
import { addPlan } from "../../services/PlanServices";
import { useRequest } from "ahooks";
import { IReqData, IModalStateProps } from "../../interfaces/Plan"
import { getTime } from "../../utils"
import PlanForm from "./PlanForm"


const AddPlan = (param: IModalStateProps) => {

  const handleSubmit = () => {
    param.handleModalLoading()
    return form.validateFields().then<IReqData>(formData => (
      {
        id: -1,
        isFinish: false,
        isRunning: false,
        name: formData.name,
        rootUrl: formData.rootUrl,
        minuteLimit: formData.minuteLimit,
        maxOccurs: formData.maxOccurs,
        maxDeep: formData.maxDeep,
        lastestRunTime: getTime()
      }
    )).catch(e => {
      console.log(e)
      message.error('输入格式错误');
      param.handleModalLoadingCancle()
      return null
    })
  }

  const addPlanAction = useRequest(
    () => addPlan(handleSubmit()),
    {
      manual: true,
      onSuccess: (result, params) => {
        message.success('添加成功');
        handleCancle();
      },
      onError: (error, params) => {
        message.error('系统错误，请重试');
        param.handleModalLoadingCancle();
      }
    })

  const handleCancle = () => {
    param.handleModalLoadingCancle()
    param.handleModalVisibleCancle()
    param.refreshData()
  }

  const [form] = Form.useForm()

  return (
    <>
      <Modal
        destroyOnClose={true}
        visible={param.visible}
        title="新增抓取计划"
        onCancel={() => handleCancle()}
        footer={[
          <Button key="back" onClick={() => handleCancle()}>
            取消
          </Button>,
          <Button key="submit" type="primary" loading={addPlanAction.loading}
            onClick={() => {addPlanAction.run()}}
          >
            提交
          </Button>,
        ]}
        >
        <PlanForm form={form}/>
      </Modal>
    </>
  )
};

export default AddPlan;
