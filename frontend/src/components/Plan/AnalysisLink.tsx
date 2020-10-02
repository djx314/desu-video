import React from "react";
import { Menu } from "antd";
import { Link } from "react-router-dom";

const AnalysisLink = (param: any) => {

  return (
    <Menu>
      <Menu.Item key={`action/${param.select}`}>
        <Link to={`/plan/action/${param.select}`}>抓取任务</Link>
      </Menu.Item>
      <Menu.Item key={`review/params/${param.select}`}>
        <Link to={`/plan/review/params/${param.select}`}>资源监控</Link>
      </Menu.Item>
      <Menu.Item key={`keyword/${param.select}`}>
        <Link to={`/plan/keyword/${param.select}?page=1&&limit=10`}>关键词搜索</Link>
      </Menu.Item>
      <Menu.Item key={`sourceKeyword/${param.select}`}>
        <Link to={`/plan/sourceKeyword/${param.select}?page=1&&limit=10`}>源码搜索</Link>
      </Menu.Item>
      <Menu.Item key={`trace/${param.select}`}>
        <Link to={`/plan/trace/${param.select}`}>溯源</Link>
      </Menu.Item>
      <Menu.Item key={`image/${param.select}`}>
        <Link to={`/plan/image/${param.select}`}>图片监控</Link>
      </Menu.Item>
    </Menu>
  );
};

export default AnalysisLink;
