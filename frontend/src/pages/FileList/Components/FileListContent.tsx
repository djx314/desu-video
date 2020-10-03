/** @jsx jsx */
import { jsx } from "@emotion/core"
import * as FileListContentStyle from "./FileListContentStyle"

interface HeadBarParameter {
    listHeight: number
}

export const FileListContent = (param: HeadBarParameter) => {
    return (
        <div css={FileListContentStyle.barContent(param.listHeight)}>
            <div css={FileListContentStyle.fileListComp(param.listHeight / 10)}>dfgetrt</div>
            <div css={FileListContentStyle.fileListComp(param.listHeight / 10)}>dfgetrt</div>
            <div css={FileListContentStyle.fileListComp(param.listHeight / 10)}>dfgetrt</div>
            <div css={FileListContentStyle.fileListComp(param.listHeight / 10)}>dfgetrt</div>
            <div css={FileListContentStyle.fileListComp(param.listHeight / 10)}>dfgetrt</div>
        </div>
    )
}