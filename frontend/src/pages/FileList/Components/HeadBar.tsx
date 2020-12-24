/** @jsx jsx */
import { jsx } from "@emotion/core"
import * as HeadBarStyle from "./HeadBarStyle"

interface HeadBarParameter {
    contentHeight: number
}

export const getBarHeight = (bodyHeight: number) => {
    return (bodyHeight / 10 > 50) ? 50 : (bodyHeight / 10)
}

export const HeadBar = (param: HeadBarParameter) => {
    return (
        <div css={HeadBarStyle.barContent(getBarHeight(param.contentHeight))}>
            影音文件浏览
        </div>
    )
}