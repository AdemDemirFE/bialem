import { CommunityMembershipManagement } from "../../../src/components/CommunityMembershipManagement";
import { ManagementEntityForm } from "../../../src/components/ManagementEntityForm";
import { managementApi, type Community } from "../../../src/lib/management-api";

const now = new Date().toISOString();
const empty: Community = { name: "", slug: "", description: "", visibility: "PUBLIC", coverImageUrl: "", communityType: "GROUP", partnerTrustLevel: "NEW", isVerifiedPartner: false, isDiscoverable: true, createdAt: now, updatedAt: now };
const visibility = [{ value: "PUBLIC", label: "Herkese açık" }, { value: "PRIVATE", label: "Özel" }, { value: "INVITE_ONLY", label: "Yalnızca davet" }];
const types = [{ value: "CATEGORY_HUB", label: "Kategori merkezi" }, { value: "PARTNER_HUB", label: "Partner merkezi" }, { value: "GROUP", label: "Grup" }];
const trust = [{ value: "NEW", label: "Yeni" }, { value: "VERIFIED", label: "Doğrulanmış" }, { value: "TRUSTED", label: "Güvenilir" }];

export default function ManagementCommunityDetail() {
  return <ManagementEntityForm
    title="Topluluk"
    listRoute="/management/communities"
    imageBucket="community-covers"
    empty={empty}
    load={managementApi.getCommunityById}
    create={managementApi.createCommunity}
    update={managementApi.updateCommunity}
    renderAfter={id => <CommunityMembershipManagement communityId={id} />}
    fields={[
      { key: "name", label: "Ad", required: true },
      { key: "slug", label: "Slug", required: true },
      { key: "description", label: "Açıklama", kind: "multiline" },
      { key: "visibility", label: "Görünürlük", kind: "select", options: visibility, required: true },
      { key: "communityType", label: "Topluluk tipi", kind: "select", options: types, required: true },
      { key: "partnerTrustLevel", label: "Partner güven seviyesi", kind: "select", options: trust, required: true },
      { key: "isVerifiedPartner", label: "Doğrulanmış partner", kind: "boolean" },
      { key: "isDiscoverable", label: "Keşfedilebilir", kind: "boolean" },
      { key: "coverImageUrl", label: "Kapak görseli", kind: "image" }
    ]}
  />;
}
