import { nomesStatus } from '../services/formatos'

function StatusBadge({ status }) {
  return <span className={'badge badge-' + status.toLowerCase()}>{nomesStatus[status] || status}</span>
}

export default StatusBadge
