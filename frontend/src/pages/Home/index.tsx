/** @jsx jsx */
import { jsx } from "@emotion/core";
import { FC, useState } from "react";
import { Layout, Menu } from "antd";
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  BookOutlined,
} from "@ant-design/icons";
import { Route, Link } from "react-router-dom";
import * as HomeStyle from "./style";
import Plan from "../Plan";
import Review from "../Analysis/Review";
import ReviewResult from "../Analysis/ReviewResult";
import Action from "../Analysis/Action";
import Keyword from "../Analysis/Search/Keyword";
import SourceKeyword from "../Analysis/Search/SourceKeyword";
import Image from "../Analysis/Image/index";

interface ICollapsedState {
  collapsed: boolean;
}

const Home: FC = () => {
  const { Header, Sider, Content, Footer } = Layout;
  const [collapsedState, setCollapsedState] = useState<ICollapsedState>({
    collapsed: false,
  });
  const toggle = () => {
    setCollapsedState({ collapsed: !collapsedState.collapsed });
  };
  return (
    <HomeStyle.GStyle>
      <Layout css={HomeStyle.HeightStyle}>
        <Sider trigger={null} collapsible collapsed={collapsedState.collapsed}>
          <div css={HomeStyle.LogoStyle} />
          <Menu theme="dark" mode="inline" defaultSelectedKeys={["1"]}>
            <Menu.Item key="1" icon={<BookOutlined />}>
              <Link to={"/plan/"}>抓取计划管理</Link>
            </Menu.Item>
            {/* <Menu.Item key="2" icon={<VideoCameraOutlined />}>
              nav 2
            </Menu.Item>
            <Menu.Item key="3" icon={<UploadOutlined />}>
              nav 3
            </Menu.Item> */}
          </Menu>
        </Sider>
        <Layout className="site-layout">
          <Header css={HomeStyle.HeaderStyle}>
            {collapsedState.collapsed ? (
              <MenuUnfoldOutlined className="trigger" onClick={toggle} />
            ) : (
              <MenuFoldOutlined className="trigger" onClick={toggle} />
            )}
          </Header>
          <Content css={HomeStyle.ContentStyle}>
            <Route path="/plan/" exact component={Plan} />
            <Route path="/plan/action/:planID" exact component={Action} />
            <Route
              path="/plan/review/params/:planID"
              exact
              component={Review}
            />
            <Route path="/plan/review" exact component={ReviewResult} />
            <Route path="/plan/keyword/:planID" exact component={Keyword} />
            <Route path="/plan/sourceKeyword/:planID" exact component={SourceKeyword} />
            <Route path="/plan/image/:planID" exact component={Image} />
          </Content>
          <Footer css={HomeStyle.TextCenter}>鹤山市网络信息中心 ©2020</Footer>
        </Layout>
      </Layout>
    </HomeStyle.GStyle>
  );
};

export default Home;
