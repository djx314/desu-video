import React, { useState } from "react";
import { Button, Modal, Input, message } from "antd";
import ClipboardJS from "clipboard";

const ShareBtn = (params: any) => {
  const [showModal, setShowModal] = useState(false);
  const [shareLink, setShareLink] = useState("");
  const clipboard = new ClipboardJS(".copy-btn");

  const cancleModal = () => {
    setShowModal(false);
  };

  const openModal = () => {
    setShareLink(params.inputText);
    setShowModal(true);
  };

  const copyShareLink = () => {
    clipboard.on("success", () => {
      clipboard.destroy();
      message.success("复制成功");
    });
    clipboard.on("error", () => {
      clipboard.destroy();
      message.error("复制失败，该浏览器不支持自动复制");
    });
  };

  const btnType = () => {
    if (params.type) {
      return params.type;
    }
    return "primary";
  };

  return (
    <>
      <Button type={btnType()} size={params.size} onClick={openModal}>
        {params.text}
      </Button>
      <Modal
        title="请复制"
        visible={showModal}
        okText="复制"
        cancelText="取消"
        onCancel={cancleModal}
        destroyOnClose
        footer={[
          <Button
            key="copy"
            type="primary"
            data-clipboard-target="#shareLinkInput"
            onClick={copyShareLink}
            className="copy-btn"
          >
            复制
          </Button>,
        ]}
      >
        <Input defaultValue={shareLink} id="shareLinkInput" />
      </Modal>
    </>
  );
};

export default ShareBtn;
