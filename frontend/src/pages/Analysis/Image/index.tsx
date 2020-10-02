/** @jsx jsx */
import { jsx } from "@emotion/core"
import * as GStyle from "./style"
import logo192 from "./wximage.jpg"
import { useState } from "react"

const arr1 = [1, 2, 3, 4]
const arr2 = [1, 2, 3, 4, 5]

const getImageCotnentHeight = () => document.documentElement.clientHeight - (64 + 24 * 4 + 70 + GStyle.titleHeight)

const str = arr1.map((item1, index1) => {
    const str2 = arr2.map((item2, index2) => {
        return (<div key={`${index1}-${index2}-inner-div`} css={GStyle.ImageElement1}><img css={GStyle.ImageStyle} src={logo192} alt="图片无法显示" /></div>)
    })
    return <div key={`${index1}-outer-div`} css={GStyle.ImageElement}>
        {str2}
    </div>
})

const Image = () => {
    const [imageCotnentHeight, setImageCotnentHeight] = useState(getImageCotnentHeight())
    window.addEventListener("resize", () => setImageCotnentHeight(getImageCotnentHeight()))

    return (<GStyle.GStyle>
        <div css={GStyle.TitleStyle}>图片监控</div>
        <div css={GStyle.ImageContent(imageCotnentHeight)}>
            <div css={GStyle.ImageContent1}>
                {str}
            </div>
        </div>
    </GStyle.GStyle>)
}

export default Image