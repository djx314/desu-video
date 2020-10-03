/** @jsx jsx */
import { jsx } from "@emotion/core"
import * as FileListContentStyle from "./FileListContentStyle"

interface HeadBarParameter {
    listHeight: number,
    fileList: Array<String>
}

export const FileListContent = (param: HeadBarParameter) => {

    const comps1 = param.fileList.map((item, index) => {
        return <div key={index} css={FileListContentStyle.fileListComp(param.listHeight / 10)}>{item}</div>
    })

    return (
        <div css={FileListContentStyle.barContent(param.listHeight)}>
            {comps1}
        </div>
    )
}