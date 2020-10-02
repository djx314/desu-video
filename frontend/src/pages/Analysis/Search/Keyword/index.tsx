import React from "react";
import Search from "../../Search";
import { searchByKeyword } from "../../../../services/AnalysisServices";

const Keyword = () => {
  const title = "关键词搜索";
  const baseUrl = "/plan/keyword/";
  return (
    <Search
      searchWith={searchByKeyword}
      searchTitle={title}
      baseUrl={baseUrl}
    />
  );
};

export default Keyword;
