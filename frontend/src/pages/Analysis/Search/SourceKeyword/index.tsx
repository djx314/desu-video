import React from "react";
import Search from "../../Search";
import { searchBySourceKeyword } from "../../../../services/AnalysisServices";

const SourceKeyword = () => {
  const title = "源码搜索";
  const baseUrl = "/plan/sourceKeyword/";
  return (
    <Search
      searchWith={searchBySourceKeyword}
      searchTitle={title}
      baseUrl={baseUrl}
    />
  );
};

export default SourceKeyword;
