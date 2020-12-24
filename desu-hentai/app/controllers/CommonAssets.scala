package assist.controllers

import javax.inject.{Inject, Singleton}

@Singleton
class CommonAssetsController @Inject() (commonAssets: controllers.Assets) {
  def staticAt(root: String, path: String) = commonAssets.at(root, path)
}
