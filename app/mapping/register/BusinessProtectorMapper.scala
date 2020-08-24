/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.register

import javax.inject.Inject
import mapping.Mapping
import mapping.reads.{BusinessProtector, BusinessProtectors}
import models.UserAnswers

class BusinessProtectorMapper @Inject()(addressMapper: AddressMapper) extends Mapping[List[ProtectorCompany]] {
  override def build(userAnswers: UserAnswers): Option[List[ProtectorCompany]] = {

    val protectors: List[BusinessProtector] =
      userAnswers.get(BusinessProtectors).getOrElse(List.empty)

    protectors match {
      case Nil => None
      case list =>
        Some(
          list.map { protector =>
            ProtectorCompany(
              name = protector.name,
              identification = buildIdentification(protector)
            )
          }
        )
    }
  }

  private def buildIdentification(protector: BusinessProtector): Option[IdentificationOrgType] = {
    (protector.utr, protector.ukAddress, protector.internationalAddress) match {
      case (None, None, None) => None
      case (Some(utr),_ , _) => Some(IdentificationOrgType(Some(utr), None))
      case (None, Some(address), _) => Some(IdentificationOrgType(None, addressMapper.build(address)))
      case (None, _, Some(address)) => Some(IdentificationOrgType(None, addressMapper.build(address)))
    }
  }
}
