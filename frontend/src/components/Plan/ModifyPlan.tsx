import React, { useEffect, useState } from "react";
import { Modal, Button, Form, message } from "antd";
import PlanForm from "./PlanForm"
import { getPlanInfo } from "../../services/PlanServices"
import { useRequest } from "ahooks";
import { IReqData, IModalStateProps } from "../../interfaces/Plan"
import { getTime } from "../../utils"
import { modifyPlan } from "../../services/PlanServices";
import { IResponse } from "../../interfaces"

const ModifyPlan = (param: IModalStateProps) => {

  const [sendReq, setSendReq] = useState(false)

  const handleSubmit = () => {
    param.handleModalLoading()
    return form.validateFields().then<IReqData>(formData => (
      {
        id: param.modifyPlanID!,
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

  const modifyPlanAction = useRequest(
    () => modifyPlan(handleSubmit()),
    {
      manual: true,
      onSuccess: (result, params) => {
        message.success('修改成功');
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

  const getPlanInfoAction = useRequest(() => getPlanInfo(param.modifyPlanID!), {
    ready: sendReq,
    refreshDeps: [param.modifyPlanID],
    formatResult: (res: IResponse<IReqData>) => {
      return res.data;
    },
    onSuccess: () => {
      setSendReq(false)
    }
  })

  const [form] = Form.useForm()

  useEffect(() => {
    if (param.modifyPlanID && param.modifyPlanID !== 0) {
      setSendReq(true)
    }
  }, [param.modifyPlanID])

  return (
    <>
      <Modal
        destroyOnClose={true}
        visible={param.visible}
        title="修改抓取计划"
        onCancel={() => handleCancle()}
        footer={[
          <Button key="back" onClick={() => handleCancle()}>
            取消
          </Button>,
          <Button key="submit" type="primary" loading={modifyPlanAction.loading}
            onClick={() => {modifyPlanAction.run()}}
          >
            提交
          </Button>
        ]}
        >
        <PlanForm form={form} infoValue={getPlanInfoAction.data} />
      </Modal>
    </>
  )
}

export default ModifyPlan