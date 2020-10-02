export interface IReviewParams {
  page?: string;
  limit?: string;
  codeArrayExclude: string;
  codeArrayInclude: string;
  hostExclude: string;
  hostInclude: string;
  protocolExclude: string;
  protocolInclude: string;
  urlExclude: string;
  urlInclude: string;
  typeExclude: string;
  typeInclude: string;
  refererExclude: string;
  refererInclude: string;
  minSize: string;
  actionId?: number;
  planId?: string;
  lastestRunTime?: string;
}

export interface IResGetActionByTime {
  id: number;
  isFinish: boolean;
  isRunning: boolean;
  planId: number;
  lastestRunTime: string;
  finishedTime: string;
}

export interface IResGetPlanUrls {
  url: string;
  contentType: string;
  referer: string;
  body: string | null;
  textBody: string | null;
  law_body: any;
  law_text_body: any;
  cssBody: any;
  host: string;
  protocol: string;
  deep: number;
  wasteTime: string;
  requestTime: string;
  status: number;
  contentLength: string;
  subs: any;
  planId: number;
  actionId: number;
  key?: string;
  _id: string;
}

export interface IPlanActionsResData {
  id: number;
  isFinish: boolean;
  isRunning: boolean;
  planId: number;
  urlsLeft: number;
  finishedTime: string | null;
  lastestRunTime: string;
  key?: any;
  isFinishText?: string;
  isRunningText?: string;
}

export interface ISearchByKeyword {
  wildcardKeyword: string;
  termKeyword: string;
  termKeywordOr: string;
  actionId: number;
  planId: number;
  page: number;
  limit: number;
}

interface IKeywordSearchResDataBody {
  url: string;
  contentType: string;
  referer: string;
  body: string | null;
  textBody: string | null;
  law_body: any;
  law_text_body: any;
  cssBody: string | null;
  host: string;
  protocol: string;
  deep: number;
  wasteTime: number;
  requestTime: number;
  status: number;
  contentLength: number;
  subs: any;
  planId: number;
  actionId: number;
}

export interface IKeywordSearchResData {
  _id: string;
  commonBody: IKeywordSearchResDataBody;
  highlight: string;
}
